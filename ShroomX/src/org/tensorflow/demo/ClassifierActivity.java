/*
 * Copyright 2016 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.demo;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Vector;
import org.tensorflow.demo.OverlayView.DrawCallback;
import org.tensorflow.demo.env.BorderedText;
import org.tensorflow.demo.env.ImageUtils;
import org.tensorflow.demo.env.Logger;
import org.tensorflow.demo.R; // Explicit import needed for internal Google builds.

public class ClassifierActivity extends CameraActivity implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();

  protected static final boolean SAVE_PREVIEW_BITMAP = false;

  private ResultsView resultsView;

  private Bitmap rgbFrameBitmap = null;
  private Bitmap croppedBitmap = null;
  private Bitmap cropCopyBitmap = null;

  private long lastProcessingTimeMs;

  // These are the settings for the original v1 Inception model. If you want to
  // use a model that's been produced from the TensorFlow for Poets codelab,
  // you'll need to set IMAGE_SIZE = 299, IMAGE_MEAN = 128, IMAGE_STD = 128,
  // INPUT_NAME = "Mul", and OUTPUT_NAME = "final_result".
  // You'll also need to update the MODEL_FILE and LABEL_FILE paths to point to
  // the ones you produced.
  //
  // To use v3 Inception model, strip the DecodeJpeg Op from your retrained
  // model first:
  //
  // python strip_unused.py \
  // --input_graph=<retrained-pb-file> \
  // --output_graph=<your-stripped-pb-file> \
  // --input_node_names="Mul" \
  // --output_node_names="final_result" \
  // --input_binary=true
  private static final int INPUT_SIZE = 224;
  private static final int IMAGE_MEAN = 117;
  private static final float IMAGE_STD = 1;
  private static final String INPUT_NAME = "input";
  private static final String OUTPUT_NAME = "final_result";


  private static final String MODEL_FILE = "file:///android_asset/output_graph.pb";
  private static final String LABEL_FILE =
      "file:///android_asset/output_labels.txt";


  private static final boolean MAINTAIN_ASPECT = true;

  private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);


  public static final String IMAGE_NAME_1 = "mushroom_img_1.jpg";
  public static final String IMAGE_NAME_2 = "mushroom_img_2.jpg";
  public static final String IMAGE_NAME_3 = "mushroom_img_3.jpg";

  public static final String MUSHROOM_NAME_1 = "mushroom_name_1.txt";
  public static final String MUSHROOM_NAME_2 = "mushroom_name_2.txt";
  public static final String MUSHROOM_NAME_3 = "mushroom_name_3.txt";

  public static final String MUSHROOM_CONFIDENCE_1 = "mushroom_conf_1.txt";
  public static final String MUSHROOM_CONFIDENCE_2 = "mushroom_conf_2.txt";
  public static final String MUSHROOM_CONFIDENCE_3 = "mushroom_conf_3.txt";



  private Integer sensorOrientation;
  private Classifier classifier;
  private Matrix frameToCropTransform;
  private Matrix cropToFrameTransform;


  private BorderedText borderedText;


  @Override
  public void onCreate(Bundle savedInstance) {
    super.onCreate(savedInstance);



  }


  @Override
  protected int getLayoutId() {
    // ====================================================
    return R.layout.camera_connection_fragment;
  }

  @Override
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }

  private static final float TEXT_SIZE_DIP = 10;

  @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {
    final float textSizePx = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);

    classifier =
        TensorFlowImageClassifier.create(
            getAssets(),
            MODEL_FILE,
            LABEL_FILE,
            INPUT_SIZE,
            IMAGE_MEAN,
            IMAGE_STD,
            INPUT_NAME,
            OUTPUT_NAME);

    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    sensorOrientation = rotation - getScreenOrientation();
    LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

    LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);
    croppedBitmap = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Config.ARGB_8888);

    frameToCropTransform = ImageUtils.getTransformationMatrix(
        previewWidth, previewHeight,
        INPUT_SIZE, INPUT_SIZE,
        sensorOrientation, MAINTAIN_ASPECT);

    cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);

    addCallback(
        new DrawCallback() {
          @Override
          public void drawCallback(final Canvas canvas) {
            renderDebug(canvas);
          }
        });
  }


  public List<Classifier.Recognition> classifyCurrentImage() {
    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
    final Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
    if (SAVE_PREVIEW_BITMAP) {
      ImageUtils.saveBitmap(croppedBitmap);
    }
    final long startTime = SystemClock.uptimeMillis();
    final List<Classifier.Recognition> results = classifier.recognizeImage(croppedBitmap);

    if (results.size() > 0)
      LOGGER.i("Results "+results.get(0).getTitle());

    lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
    LOGGER.i("Detect: %s", results);
    cropCopyBitmap = Bitmap.createBitmap(croppedBitmap);

    requestRender();
    readyForNextImage();

    saveImageAndDeleteExtra(croppedBitmap);
    refreshMetaData(results);


    return results;
  }


  private void refreshMetaData(List<Classifier.Recognition> results) {
    String name = results.get(0).getTitle();
    float conf = results.get(0).getConfidence();
    int confint = (int)(conf * 100);
    String confidence = Integer.toString(confint);

    String name1 = readFromFile(MUSHROOM_NAME_1);
    String name2 = readFromFile(MUSHROOM_NAME_2);
    String conf1 = readFromFile(MUSHROOM_CONFIDENCE_1);
    String conf2 = readFromFile(MUSHROOM_CONFIDENCE_2);

    writeToFile(name,MUSHROOM_NAME_1);
    writeToFile(confidence,MUSHROOM_CONFIDENCE_1);
    writeToFile(name1,MUSHROOM_NAME_2);
    writeToFile(conf1,MUSHROOM_CONFIDENCE_2);
    writeToFile(name2,MUSHROOM_NAME_3);
    writeToFile(conf2,MUSHROOM_CONFIDENCE_3);

  }

  private void saveImageAndDeleteExtra(Bitmap bitmap) {
    LOGGER.i("Save Bitmap got called");
    Bitmap img1 = loadImageFromStorage(IMAGE_NAME_1);
    Bitmap img2 = loadImageFromStorage(IMAGE_NAME_2);
    String absPath = saveToInternalStorage(bitmap,IMAGE_NAME_1);
    LOGGER.i("Save Bitmap "+absPath);
    if (img1 != null) {saveToInternalStorage(img1,IMAGE_NAME_2);}
    if (img2 != null) {saveToInternalStorage(img2,IMAGE_NAME_3);}
  }

  private String saveToInternalStorage(Bitmap bitmapImage,String filename){
    ContextWrapper cw = new ContextWrapper(getApplicationContext());
    // path to /data/data/yourapp/app_data/imageDir
    File directory = getApplicationContext().getFilesDir();
    // Create imageDir
    File mypath=new File(directory,filename);

    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(mypath);
      // Use the compress method on the BitMap object to write image to the OutputStream
      bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.i("Bitmap Store fail");
    } finally {
      try {
        fos.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return directory.getAbsolutePath();
  }

  private Bitmap loadImageFromStorage(String filename)
  {

    try {
      File directory = getApplicationContext().getFilesDir();
      File f=new File(directory, filename);
      Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
      return b;
    }
    catch (FileNotFoundException e)
    {
      e.printStackTrace();
      LOGGER.i("Bitmap Load fail");
    }
    return null;
  }

  private void writeToFile(String data, String filename) {
    try {
      OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(filename, MODE_PRIVATE));
      outputStreamWriter.write(data);
      outputStreamWriter.close();
    }
    catch (IOException e) {
      Log.e("Exception", "File write failed: " + e.toString());
    }
  }

  private String readFromFile(String filename) {

    String ret = "";

    try {
      //File directory = getApplicationContext().getFilesDir();
      //File f=new File(directory, filename);
      InputStream inputStream = openFileInput(filename);

      if ( inputStream != null ) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String receiveString = "";
        StringBuilder stringBuilder = new StringBuilder();

        while ( (receiveString = bufferedReader.readLine()) != null ) {
          stringBuilder.append(receiveString);
        }

        inputStream.close();
        ret = stringBuilder.toString();
      }
    }
    catch (FileNotFoundException e) {
      Log.e("login activity", "File not found: " + e.toString());
    } catch (IOException e) {
      Log.e("login activity", "Can not read file: " + e.toString());
    }

    return ret;
  }


  @Override
  protected void processImage() {
    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
    final Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);

    // For examining the actual TF input.
    if (SAVE_PREVIEW_BITMAP) {
      ImageUtils.saveBitmap(croppedBitmap);
    }

  }



  @Override
  public void onSetDebug(boolean debug) {
    classifier.enableStatLogging(debug);
  }

  private void renderDebug(final Canvas canvas) {
    if (!isDebug()) {
      return;
    }
    final Bitmap copy = cropCopyBitmap;
    if (copy != null) {
      final Matrix matrix = new Matrix();
      final float scaleFactor = 2;
      matrix.postScale(scaleFactor, scaleFactor);
      matrix.postTranslate(
          canvas.getWidth() - copy.getWidth() * scaleFactor,
          canvas.getHeight() - copy.getHeight() * scaleFactor);
      canvas.drawBitmap(copy, matrix, new Paint());

      final Vector<String> lines = new Vector<String>();
      if (classifier != null) {
        String statString = classifier.getStatString();
        String[] statLines = statString.split("\n");
        for (String line : statLines) {
          lines.add(line);
        }
      }

      lines.add("Frame: " + previewWidth + "x" + previewHeight);
      lines.add("Crop: " + copy.getWidth() + "x" + copy.getHeight());
      lines.add("View: " + canvas.getWidth() + "x" + canvas.getHeight());
      lines.add("Rotation: " + sensorOrientation);
      lines.add("Inference time: " + lastProcessingTimeMs + "ms");

      borderedText.drawLines(canvas, 10, canvas.getHeight() - 10, lines);
    }
  }
}

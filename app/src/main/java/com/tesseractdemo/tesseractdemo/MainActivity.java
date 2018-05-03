package com.tesseractdemo.tesseractdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    String whitelist = " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    String whitelist2 = " abcdefghijklmnopqrstuvwxyz";
    String currentState = "Qty";
    String mealName = "";
    String quantity_str = "";
    String price_str = "";
    Integer quantity = 0;
    double price = 0;

    static ProcessImage processImg = new ProcessImage();
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Uri file = null;
    Bitmap selectedImage = null;
    ImageView imageView;
    Button uploadBtn, recognizeBtn;
    TextView myImageViewText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageToUpload);
        //imageView.setBackgroundColor(Color.rgb(220, 220, 220));
        recognizeBtn = (Button) findViewById(R.id.recognizeBtn);
        myImageViewText = (TextView) findViewById(R.id.myImageViewText);


        if (!OpenCVLoader.initDebug()) {
            Log.e(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), not working.");
        } else {
            Log.d(this.getClass().getSimpleName(), "  OpenCVLoader.initDebug(), working.");
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            uploadBtn.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                uploadBtn.setEnabled(true);
            }
        }
    }

    String mCurrentPhotoPath = "";

    public void takePicture(View view) {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.i("ERRRRRRRROOOOORRRRR", "IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = DateFormat.getDateTimeInstance().format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                imageView.setImageBitmap(selectedImage);
                myImageViewText.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void recognizeBtnClick(View v) {

//        OcrManager manager = new OcrManager();
//        manager.initAPI();
//        String textRec = manager.startRecognize(selectedImage);
//        recognizedText.setText(textRec, TextView.BufferType.EDITABLE);

        //parseText(textRec);

        if (selectedImage == null) {

            Toast.makeText(MainActivity.this, "Please Capture Image", Toast.LENGTH_LONG).show();

        } else {

            Bitmap newImg = processImg.process(selectedImage);
            //newImgView.setImageBitmap(newImg);

            OcrManager manager = new OcrManager();
            manager.initAPI();
            String textRec = manager.startRecognize(newImg);
            Log.d("Before ParseText", textRec);
            //recognizedText.setText(textRec, TextView.BufferType.EDITABLE);
//
            ArrayList<FoodOrder> orderList = new ArrayList<FoodOrder>();
            orderList = parseText(textRec);
            Log.d("ARRAYYYY222", Integer.toString(orderList.size()));
            // TODO Auto-generated method stub
            //Intent i = new Intent(getApplicationContext(),Main2Activity.class);
            Intent i = new Intent(MainActivity.this, Main2Activity.class);
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("Order List From Main1", orderList); // Be sure con is not null here
            i.putExtras(bundle);
            //startActivity(i);
            MainActivity.this.startActivity(i);
            setContentView(R.layout.activity_main);
        }
    }

    //public void parseText(String text) {
    public ArrayList<FoodOrder> parseText(String text) {

        String str = "";

        ArrayList<FoodOrder> orderList = new ArrayList<FoodOrder>();

        String SearchForQty = "Qty";
        String SearchForMealName = "Meal Name";
        String SearchForSubtotal = "Subtotal";
        String newText2 = "";
        Boolean Found = false;

        Log.d("Before First If", "BBBBBBBBBB");

        if (text.indexOf(SearchForQty) >= 0) {
            newText2 = text.substring(text.indexOf(SearchForQty));
            Found = true;
        } else if (text.indexOf(SearchForMealName) >= 0) {
            newText2 = text.substring(text.indexOf(SearchForMealName));
            Found = true;
        } else if (text.indexOf(SearchForSubtotal) >= 0) {
            newText2 = text.substring(text.indexOf(SearchForSubtotal));
            Found = true;
        } else {
            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

        Log.d("Before 2nd if", "CCCCCCCCCC");

        if (Found) {
            String substr = "\n";
            String LineAfterQty = newText2.substring(newText2.indexOf(substr) + 1);

            String BeforeNet = "Order Net Total";

            String finalText = LineAfterQty.substring(0, LineAfterQty.indexOf(BeforeNet));
            Log.d("NEW TEXT", finalText);

            String showWhiteSpace = finalText;
            showWhiteSpace = showWhiteSpace.replace("\n", "newLine");
            showWhiteSpace = showWhiteSpace.replace("\t", "TAB");
            showWhiteSpace = showWhiteSpace.replace("\f", "FAN");
            showWhiteSpace = showWhiteSpace.replace("\r", "RAN");
            showWhiteSpace = showWhiteSpace.replace(" ", "----");

            //Log.d("SHOW", showWhiteSpace);
            Integer num;
            //Log.d("Original Text", newText);
            for (char c : finalText.toCharArray()) {

                //Log.d("CHARR", Character.toString(c));
                if ((whitelist.indexOf(c) > 0) || (whitelist2.indexOf(c) > 0) || Character.isWhitespace(c)) {

                    String charType_str = charType(c);
                    switch (currentState) {
                        case "Qty": {
                            switch (charType_str) {
                                case "letter": {
                                    quantity = 1;
                                    currentState = "mealName";
                                    mealName += c;
                                    str += c;
                                    Log.d("122", str);
                                    break;
                                }
                                case "digit": {
                                    quantity_str += c;
                                    str += c;
                                    Log.d("127", str);
                                    Log.d("127", currentState);
                                    break;
                                }
                                case "whitespace": {
                                    if(quantity_str == ""){
                                        break;
                                    }else{
                                    quantity = Integer.parseInt(quantity_str);
                                    currentState = "mealName";
                                    //str +=c;
                                    //Log.d("133",str);
                                    break;}
                                }
                                default: {
                                    continue;
                                }
                            }
                            break;
                        }
                        case "mealName": {
                            switch (charType_str) {
                                case "letter": {
                                    mealName += c;
                                    str += c;
                                    Log.d("144", str);
                                    break ;
                                }
                                case "digit": {
                                    if (mealName == "") {
                                        mealName += c;
                                        str += c;
                                        Log.d("150", str);
                                        Log.d("150", currentState);
                                    } else {
                                        if (mealName == "Delivery Charge")
                                            break;
                                        currentState = "price";
                                        price_str += c;
                                        str += c;
                                        Log.d("155", str);
                                        Log.d("155", currentState);
                                    }
                                    break;
                                }
                                case "whitespace": {
                                    mealName += c;
                                    str += c;
                                    Log.d("161", str);
                                    break;
                                }
                                default:
                                    continue;
                            }
                            break;
                        }
                        case "price": {
                            switch (charType_str) {
                                case "digit": {
                                    price_str += c;
                                    str += c;
                                    Log.d("172", str);
                                    Log.d("172", str);
                                    Log.d("172", currentState);
                                    currentState = "price";
                                    break;
                                }
                                case "symbol": {
                                    price_str += c;
                                    str += c;
                                    Log.d("177", str);
                                    currentState = "price";
                                    break;
                                }
                                /*case "whitespace": {
                                    //   str +=c;
                                    //   Log.d("181",str);
                                    break;
                                }*/
                                case "new line": {
                                    if(price_str.equals("")){
                                        //price = 0.0;
                                        currentState = "Qty";
                                        break;
                                    }

                                    price_str = price_str.substring(0, price_str.length()-2) + "." + price_str.substring(price_str.length()-2);
                                    Log.d("Price",price_str);


                                    price = Double.parseDouble(price_str);
                                /*DecimalFormat df = new DecimalFormat("0.00");
                                df.setMaximumFractionDigits(2);
                                price_str = df.format(price);*/
                                    if (quantity != 1)
                                        quantity = Integer.parseInt(quantity_str);
                                /*if(price != 0.0 ){
                                    FoodOrder order = new FoodOrder(mealName, quantity, price);
                                    orderList.add(order);
                                }
                                if(price == 0.0){

                                }*/
                                    FoodOrder order = new FoodOrder(mealName, quantity, price);
                                    orderList.add(order);
                                    mealName = "";
                                    quantity_str = "";
                                    price_str = "";
                                    quantity = 0;
                                    price = 0;
                                    //str +=c;
                                    //Log.d("215",str);
                                    currentState = "Qty";
                                    break;
                                }
                                default:
                                    continue;
                            }
                            break;
                        }
                        default:{
                            continue;
                        }
                    }
                }
            }


            //Log.d("List", Arrays.toString(orderList.toArray()));
            Log.d("HELLOOOOO", "");
            int k;
            //ArrayList<Integer> indicesOfOrdersToRemove = new ArrayList<Integer>();
            Double Count = 0.0;
            Integer temp = 0;
            for (k = 0; k < orderList.size(); k++) {

                if (orderList.get(k).getPrice() == 0.0) {
                    temp = k;
                    Log.d("k before while", Integer.toString(k));
                    while (orderList.get(k).getPrice() == 0.0) {
                        Count++;
                        k++;
                    }
                    k = temp;
                    Log.d("k after while", Integer.toString(k));
                    for (int j = k; j < k + Count; j++) {
                        orderList.get(j).setPrice(orderList.get(k - 1).getPrice() / Count);
                    }
                    Count = 0.0;
                    orderList.remove(k - 1);
                }
                //Log.d("List", orderList.get(k).printOrder());
                k = k + Count.intValue();
            }
            for (int m = 0; m < orderList.size(); m++) {
                Log.d("List", orderList.get(m).printOrder());
                Log.d("FINAAAAAAAAAAAAAAAAL", "");
            }
        }
        return orderList;
    }


    public String charType(char c) {

        if (Character.isDigit(c))
            return "digit";
        else if (Character.isLetter(c))
            return "letter";
        else if (c == ' ')
            return "whitespace";
        else if (String.valueOf(c).matches("\n"))
            return "new line";
        else if (c == '.')
            return "symbol";
        else
            return "other";
    }


}
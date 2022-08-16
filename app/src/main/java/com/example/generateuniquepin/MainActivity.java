package com.example.generateuniquepin;

import static com.example.generateuniquepin.All_External_ReadWrite_Permission.ReadWrite_TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements MainActivity_I{

    EditText lowerLimit_EdTxt, higherLimit_EdTxt, fileName_edTxt;
    TextView generatedPin_Txt, led_txt, mainFolderLocation_Txt;
    String SP_NAME = "All_Pins", previousFileName = "";
    //Set<String> all_pins_set = new HashSet<>();
    ArrayList<String> all_pins_arr = new ArrayList<>();
    String generatedPin_Str = "";
    boolean is_newPin_Unique = false;
    int lowLimit = 0, highLimit = 0;
    ImageView ledPic;
    Spinner serial_FirstPart_spinner, serial_SecondPart_spinner, serial_ThirdPart_spinner;

    Switch uniquePin_Sw, hexAlso_Sw;
    HSSFWorkbook hssfWorkbook;
    HSSFSheet hssfSheet;
    HSSFRow hssfRow;
    HSSFCell hssfCell;
    //All_External_ReadWrite_Permission all_external_readWrite_permission;



    // Getting permission to read and write to folder in android 10+ is very difficult but this solution worked for me well :
    // https://www.youtube.com/watch?v=WAgbkQaqHp4





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        lowerLimit_EdTxt = findViewById(R.id.lower_limit_edtxt);
        higherLimit_EdTxt= findViewById(R.id.higher_limit_edtxt);
        generatedPin_Txt  = findViewById(R.id.generatedPin_Txt);
        led_txt  = findViewById(R.id.ledName_heading);
        fileName_edTxt  = findViewById(R.id.fileName_edtxt);
        ledPic = findViewById(R.id.led_pic);
        mainFolderLocation_Txt = findViewById(R.id.mainFolderPath_heading);
        serial_FirstPart_spinner = findViewById(R.id.serial_FirstPart_spinner);
        serial_SecondPart_spinner = findViewById(R.id.serial_SecondPart_spinner);
        serial_ThirdPart_spinner = findViewById(R.id.serial_ThirdPart_spinner);

        lowerLimit_EdTxt.setTransformationMethod(null);
        higherLimit_EdTxt.setTransformationMethod(null);
        fileName_edTxt.setTransformationMethod(null);


        //create a list of items for the spinner.
        String[] items = new String[]{"TEBEHE", "TEBDHE", "TEBEHD", "TEBDHD", "TDBEHE", "TDBDHE", "TDBDHD"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        serial_FirstPart_spinner.setAdapter(adapter);

        //create a list of items for the spinner.
        String[] items1 = new String[]{ "N", "M", "Q", "R", "V", "W"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items1);
        //set the spinners adapter to the previously created one.
        serial_SecondPart_spinner.setAdapter(adapter1);

        //create a list of items for the spinner.
        String[] items2 = new String[]{ "S1", "S2", "S4", "S8"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items2);
        //set the spinners adapter to the previously created one.
        serial_ThirdPart_spinner.setAdapter(adapter2);


        fileName_edTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ledPic.setBackgroundResource(R.drawable.led_off);
                led_txt.setTextColor(getResources().getColor(R.color.gray));
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        previousFileName = "";

        uniquePin_Sw = findViewById(R.id.uniquePin_Switch);
        hexAlso_Sw = findViewById(R.id.generateHex_Switch);

        uniquePin_Sw.setOnCheckedChangeListener((compoundButton, b) -> {});
        hexAlso_Sw.setOnCheckedChangeListener((compoundButton, b) -> {
            int d = 131381837;
            Toast.makeText(this, " to hex = " + Integer.toString(d, 16), Toast.LENGTH_SHORT).show();
        });

        hssfWorkbook = new HSSFWorkbook();
        hssfSheet = hssfWorkbook.createSheet();
        hssfRow = hssfSheet.createRow(0);
        hssfCell = hssfRow.createCell(0);

        ledPic.setBackgroundResource(R.drawable.led_off);
        led_txt.setTextColor(getResources().getColor(R.color.gray));
        fileName =  default_fileName;

/*
        all_external_readWrite_permission = new All_External_ReadWrite_Permission(this, this, this);
        all_external_readWrite_permission.check_Or_RequestPermission__Then__createFolder();
        */

      //  ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
       //         Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);



        generatedPin_Str = "";
        is_newPin_Unique = false;
        set_Lower_and_Higher_Limit(true);

        higherLimit_EdTxt.setHint(String.valueOf(default_highLimit));
        lowerLimit_EdTxt.setHint(String.valueOf(default_lowLimit));
        fileName_edTxt.setHint(default_fileName);

       // SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
       // SharedPreferences.Editor ed = sp.edit();

       // all_pins_set = sp.getStringSet(SP_NAME, new HashSet<>());
        //all_pins_set = new HashSet<>();

        all_pins_arr = new ArrayList<>();

        findViewById(R.id.openFolder_imageButton).setOnClickListener(view -> {
            set_FileAndFolderName();

            /*Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("file://" + "/" + folderName);
                    //+  File.separator + folderName + File.separator);
            intent.setDataAndType(uri, "application//");
            startActivity(intent);
            */


           // String sPath = Environment.getExternalStorageDirectory() + "/" + mainFolderName + "/";
           // Uri u = Uri.parse(sPath);
           // Intent i = new Intent(Intent.ACTION_PICK);
           // i.setDataAndType(u, "*/*");
           // startActivity(i);

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setDataAndType(Uri.parse(Environment.getExternalStorageDirectory() +  File.separator + mainFolderName + File.separator), "*/");
            startActivity(intent);




           /* if(permissionGranted()) {
                SingleFilePickerDialog singleFilePickerDialog = new SingleFilePickerDialog(this,
                        () -> Toast.makeText(MainActivity.this, "Canceled!!", Toast.LENGTH_SHORT).show(),
                        files -> Toast.makeText(MainActivity.this, files[0].getPath(), Toast.LENGTH_SHORT).show());
                singleFilePickerDialog.show();
            }
            else{
                requestPermission();
            }
            */
        });

        findViewById(R.id.generatePin_Button).setOnClickListener(view -> {
            do { generateUniquePin__forThisSession(); }
            while (!is_newPin_Unique);

            generatedPin_Txt.setText(generatedPin_Str);

            //Toast.makeText(this, "lower limit :" + lowLimit + " high limit : " + highLimit, Toast.LENGTH_SHORT).show();
        });

         findViewById(R.id.getPermission_button).setOnClickListener(view -> {
             Intent i = new Intent(this, MainActivity2.class);
             startActivity(i);
         });

        /*findViewById(R.id.save_generatedPin__Button).setOnClickListener(view -> {
            if (generatedPin_Str == null || generatedPin_Str.isEmpty()) return;

            all_pins_set.add(generatedPin_Str);
            ed.putStringSet(SP_NAME, all_pins_set);
            ed.apply();
        });
        */

        findViewById(R.id.delete__DB_or_Session__Button).setOnClickListener(view -> {
            all_pins_arr.clear();
            all_pins_arr = new ArrayList<>();


            //all_pins_set.clear();
            //all_pins_set = new HashSet<>();

            /*
            ed.clear();
            ed.apply();
            */
        });

        findViewById(R.id.generate_all_pin_low_to_high___Download_xl_Button).setOnClickListener(view -> {
            String fileName = fileName_edTxt.getText().toString().trim();
            if (previousFileName != null && !previousFileName.isEmpty() && previousFileName.equals(fileName)) {
                Toast.makeText(this, "Please Change The File Name", Toast.LENGTH_SHORT).show();
                ledPic.setBackgroundResource(R.drawable.led_off);
                led_txt.setTextColor(getResources().getColor(R.color.gray));
                return;
            }

            previousFileName = fileName;

            set_Lower_and_Higher_Limit(false);

            if (highLimit <= lowLimit) {
                Toast.makeText(this, "Lower Limit Can't be Greater then Higher Limit", Toast.LENGTH_SHORT).show();
                ledPic.setBackgroundResource(R.drawable.led_off);
                led_txt.setTextColor(getResources().getColor(R.color.gray));
                return;
            }
            if ((highLimit - lowLimit) >= 65500) {
                Toast.makeText(this, "(Higher - Lower) Difference Can't be greater then 65500", Toast.LENGTH_SHORT).show();
                ledPic.setBackgroundResource(R.drawable.led_off);
                led_txt.setTextColor(getResources().getColor(R.color.gray));
                return;
            }


            Toast.makeText(this, "lower limit : " + lowLimit + " High limit : " + highLimit, Toast.LENGTH_SHORT).show();

            all_pins_arr.clear();
            all_pins_arr = new ArrayList<>();

            ledPic.setBackgroundResource(R.drawable.led_off);
            led_txt.setTextColor(getResources().getColor(R.color.gray));


            //all_pins_set.clear();
           // all_pins_set = new HashSet<>();



            //create_Excel_File();
            //Toast.makeText(this, "Ready to Download", Toast.LENGTH_SHORT).show();

            new Thread(() -> {

                for (int i = lowLimit; i <(highLimit + 1); i++) {
                    do { generateUniquePin__forThisSession(); }
                    while (!is_newPin_Unique);

                    //all_pins_set.add(generatedPin_Str);
                    all_pins_arr.add(generatedPin_Str);
                }


                create_Excel_File();

                MainActivity.this.runOnUiThread(() -> {
                    //update the UI on main thread

                    Toast.makeText(this, "Ready to Download", Toast.LENGTH_SHORT).show();
                    ledPic.setBackgroundResource(R.drawable.led_on);
                    led_txt.setTextColor(getResources().getColor(R.color.green));

                });
            }).start();


            /*
            ed.putStringSet(SP_NAME, all_pins_set);
            ed.apply();
            */


        });


        create_Main_Folder();
    }


    private String smallLetter_To_Capital(String s) {
        //String converted = "";

        return  s.toUpperCase();

        //return s;

        /*for (int i = 0; i < s.length(); i++) {
            String a;
            if (!Character.isDigit(s.charAt(i))) {
                a = Character.toUpperCase(s.charAt(i));
            }
            converted +=


        }
        */

    }


    private static final String mainFolderName = "IramPinGenerator";

    private void create_Main_Folder() {


        String fullName =  "device storage / " + mainFolderName;
        mainFolderLocation_Txt.setText(fullName);
        File mainFilePath = new File(Environment.getExternalStorageDirectory() ,"/" + mainFolderName);
        //File mainFilePath = new File(Environment.getExternalStorageDirectory() + "/mySheetIram1");
        boolean isFile_Created = mainFilePath.mkdir();

        if (isFile_Created) {
            //Toast.makeText(this, "folder Created", Toast.LENGTH_SHORT).show();
        }
        else {
            //Toast.makeText(this, "folder not created", Toast.LENGTH_SHORT).show();
        }

        /*
        try {
            if (!mainFilePath.exists()) {
                Toast.makeText(this, "folder does not exists", Toast.LENGTH_SHORT).show();
                isFile_Created = mainFilePath.mkdir();
            }
            else {
                Toast.makeText(this, "folder exists", Toast.LENGTH_SHORT).show();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(mainFilePath);
            fileOutputStream.write(0);

            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "folder create failed : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        */
    }


    //Workbook wb = new XSSFWorkbook()

    final int default_highLimit = 9999, default_lowLimit = 0;


    private void set_Lower_and_Higher_Limit(boolean set_OnlyDefault) {
        lowLimit = default_lowLimit;
        highLimit = default_highLimit;

        if (set_OnlyDefault) return;

        if (!lowerLimit_EdTxt.getText().toString().trim().isEmpty()) {
            try {
                lowLimit = Integer.parseInt(lowerLimit_EdTxt.getText().toString().trim());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!higherLimit_EdTxt.getText().toString().trim().isEmpty()) {
            try {
                highLimit = Integer.parseInt(higherLimit_EdTxt.getText().toString().trim());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }


    }



    // https://stackoverflow.com/a/21049922
    // https://stackoverflow.com/questions/21049747/how-can-i-generate-a-random-number-in-a-certain-range

    private void generateUniquePin__forThisSession() {
        set_Lower_and_Higher_Limit(false);
        //int uniquePin = new Random().nextInt(((highLimit - lowLimit) +1) + lowLimit);
        int uniquePin = new Random().nextInt(((highLimit - lowLimit) +1)) + lowLimit;
        String uniquePin_Str = String.valueOf(uniquePin);
        uniquePin_Str = get5Digit_Pin(uniquePin_Str);



       /* if (ignoreVal != null && uniquePin_Str.contains(ignoreVal.toString())) {
            is_newPin_Unique = false;
            generatedPin_Str = "00000";
            return;
        }
        */

        if (uniquePin_Sw.isChecked()) {
            if (//(all_pins_set.contains(uniquePin_Str))
                (all_pins_arr.contains(uniquePin_Str))
            ) {
                is_newPin_Unique = false;
                generatedPin_Str = "00000";
            }
            else {
                is_newPin_Unique = true;
                generatedPin_Str = String.valueOf(uniquePin);
                generatedPin_Str = get5Digit_Pin(generatedPin_Str);
            }
        }
        else {
            is_newPin_Unique = true;
            generatedPin_Str = String.valueOf(uniquePin);
            generatedPin_Str = get5Digit_Pin(generatedPin_Str);
        }



       /* if (//(all_pins_arr.contains(uniquePin_Str))
                (all_pins_set.contains(uniquePin_Str))
                        || (ignoreVal != null && uniquePin_Str.contains(ignoreVal.toString()))) {
            is_newPin_Unique = false;
            generatedPin_Str = "00000";
        }
        else {
            is_newPin_Unique = true;
            generatedPin_Str = String.valueOf(uniquePin);
            if (generatedPin_Str.length() == 1) generatedPin_Str = "0000" + generatedPin_Str;
            if (generatedPin_Str.length() == 2) generatedPin_Str = "000" + generatedPin_Str;
            if (generatedPin_Str.length() == 3) generatedPin_Str = "00" + generatedPin_Str;
            if (generatedPin_Str.length() == 4) generatedPin_Str = "0" + generatedPin_Str;
        }
        */

    }


    private String get5Digit_Pin(String pin) {
        if (pin.length() == 1) pin = "0000" + pin;
        if (pin.length() == 2) pin = "000" + pin;
        if (pin.length() == 3) pin = "00" + pin;
        if (pin.length() == 4) pin = "0" + pin;
        return pin;
    }



    private String getHexValue(String val) {
        try {
            int i = Integer.parseInt(val);
            i = i + 43;
            //return Integer.toString(i, 16);
            return Integer.toHexString(i);
            //val.format("#%06X", (0xFFFFFF & colorYellow));

        }
        catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return "";
    }


    private String getSerialStr(int i) {
        return (String) serial_FirstPart_spinner.getSelectedItem() + serial_SecondPart_spinner.getSelectedItem() + i + serial_ThirdPart_spinner.getSelectedItem();
    }


   // private final File filePath = new File(Environment.getExternalStorageDirectory() , "/UniquePin_Sheet/UniquePin.xlsx");
    private File filePath = new File(Environment.getExternalStorageDirectory() , "/UniquePin16.xls");
    private final String default_fileName = "UniquePin16";
    String fileName;

    private void create_Excel_File() {
        set_FileAndFolderName();

        /*
        // create folder
        filePath = new File(Environment.getExternalStorageDirectory() , "/" + mainFolderName + "/" + folderName);
        boolean f_Created = filePath.mkdir();
        */

        filePath = new File(Environment.getExternalStorageDirectory() , "/" + mainFolderName + "/" + fileName + ".xls");

        hssfWorkbook = new HSSFWorkbook();
        hssfSheet = hssfWorkbook.createSheet();
        hssfRow = hssfSheet.createRow(0);
        hssfCell = hssfRow.createCell(0);

        /*HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet hssfSheet = hssfWorkbook.createSheet();

        HSSFRow hssfRow = hssfSheet.createRow(0);
        HSSFCell hssfCell = hssfRow.createCell(0);
        */

        //hssfCell.setCellValue("Unique Pin");
        hssfRow = hssfSheet.createRow(0);    hssfCell = hssfRow.createCell(0);    hssfCell.setCellValue("Random Pin");
        hssfCell = hssfRow.createCell(1);    hssfCell.setCellValue("Hex of Pin");
        hssfCell = hssfRow.createCell(2);    hssfCell.setCellValue("Unique Serial");

        for (int i = 0; i < all_pins_arr.size(); i++) {
            hssfRow = hssfSheet.createRow(i+1);
            hssfCell = hssfRow.createCell(0);
            hssfCell.setCellValue(all_pins_arr.get(i));

            Log.d(ReadWrite_TAG, "create_Excel_File: " + i);


            hssfCell = hssfRow.createCell(2);
            hssfCell.setCellValue(getSerialStr(i));


            if (hexAlso_Sw.isChecked()) {
                //hssfRow = hssfSheet.createRow(i);

                hssfCell = hssfRow.createCell(1);
                hssfCell.setCellValue(smallLetter_To_Capital(getHexValue(all_pins_arr.get(i))));
            }


            if (i == 1) {
                hssfCell = hssfRow.createCell(5);
                hssfCell.setCellValue("Encoding Formula");
            }
            else if (i == 2) {
                hssfCell = hssfRow.createCell(5);
                hssfCell.setCellValue("(Pin + 43)...... then...... ConvertToHex");
            }
            else if (i == 4) {
                hssfCell = hssfRow.createCell(5);
                hssfCell.setCellValue("Decoding Formula");
            }
            else if (i == 5) {
                hssfCell = hssfRow.createCell(5);
                hssfCell.setCellValue("Convert Hex To Int.....then..... (Int - 43) = pin  ");
            }


        }

        /*
        hssfRow = hssfSheet.createRow(0);
        hssfCell = hssfRow.createCell(3);
        hssfCell.setCellValue("Encoding Formula");

        hssfRow = hssfSheet.createRow(1);
        hssfCell = hssfRow.createCell(3);
        hssfCell.setCellValue("(Pin + 43) then ConvertToHex");

        hssfRow = hssfSheet.createRow(3);
        hssfCell = hssfRow.createCell(3);
        hssfCell.setCellValue("Decoding Formula");

        hssfRow = hssfSheet.createRow(4);
        hssfCell = hssfRow.createCell(3);
        hssfCell.setCellValue("Hex then ConvertToInt (Int - 43) = pin  ");
        */



        /*int i = 0;
        for (String s : all_pins_set) {
            hssfRow = hssfSheet.createRow(i);
            hssfCell = hssfRow.createCell(0);

            hssfCell.setCellValue(s);

            Log.d(ReadWrite_TAG, "create_Excel_File: " + i);
            i++;

        }
        */
        //----------------------

        Log.d(ReadWrite_TAG, //"set size : " + all_pins_set.size() +
                "..... arr size : " + all_pins_arr.size());

        boolean isFile_Created = false;
        String check = "";

        try {
            if (!filePath.exists()){
                isFile_Created = filePath.createNewFile();
                check = "1";
            }
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            hssfWorkbook.write(fileOutputStream);

            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
                check +="2";
            }
        }
        catch (Exception e){
            e.printStackTrace();
            check = e.toString();
        }

        Log.d(ReadWrite_TAG, "create_Excel_File: "+ check + "...." + isFile_Created + "......." + filePath.exists() //+ "....." + f_Created
        );
        //Toast.makeText(this, check + "...." + isFile_Created + "......." + filePath.exists(), Toast.LENGTH_SHORT).show();
    }


    void set_FileAndFolderName() {
        fileName = fileName_edTxt.getText().toString().trim();
        if (fileName.isEmpty()) { fileName =  default_fileName; }
    }

    private boolean isExternalStorageWriteable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }





    //region for file picker library ......
    private boolean permissionGranted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }
    // endregion




    // from main2 ---------------------------------------


    @Override
    public void use_storageActivityResultLauncher(Intent i) {
      //  storageActivityResultLauncher.launch(i);
    }
    /*

    public ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d(ReadWrite_TAG, "onActivityResult: ");
                    // here we will handle the result of our intent
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        // Android is R or above
                        if (Environment.isExternalStorageManager()) {
                            // Manage External Storage Permission is granted
                            Log.d(ReadWrite_TAG, "onActivityResult: Manage External Storage Permission is granted");
                            all_external_readWrite_permission.createFolder();
                        }
                        else {
                            // Manage External Storage Permission is denied
                            Log.d(ReadWrite_TAG, "onActivityResult: Manage External Storage Permission is denied");
                            Toast.makeText(MainActivity.this, "Manage External Storage Permission is denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        // Android is below R

                    }
                }
            }
    );

    // handle the permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ReadWrite_CODE) {
            if (grantResults.length > 0) {
                // check each permission if granted
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (write && read) {
                    //External Storage Permission granted
                    Log.d(ReadWrite_TAG, "onRequestPermissionsResult: External Storage Permission granted");
                    all_external_readWrite_permission.createFolder();
                }
                else {
                    //External Storage Permission denied
                    Log.d(ReadWrite_TAG, "onRequestPermissionsResult: External Storage Permission denied");
                    Toast.makeText(this, "External Storage Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    */
}
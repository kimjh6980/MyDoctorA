package com.example.khseob0715.sanfirst.UserActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khseob0715.sanfirst.R;
import com.example.khseob0715.sanfirst.ServerConn.SignUp;

/**
 * Created by Aiden on 2018-02-08.
 */

public class SignUPActivity extends AppCompatActivity implements Button.OnClickListener{
    private RadioButton maleRadio, femaleRadio;
    private TextView selected_date, E_mailText;
    private Button Duplicate_Btn, Send_Email_Btn, Admit_Btn, SignUP_Btn;
    private DatePickerDialog datePickerDialog;
    private LinearLayout Verification_Layout, Personal_Layout;

    private ImageView calendar;

    private String User_email;
    private EditText  password, confirm_password, Fname, Lname, phone_num;

    SignUp signup = new SignUp();

    private int gender = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Button
        maleRadio = (RadioButton)findViewById(R.id.radioButton);
        maleRadio.setOnClickListener(this);

        femaleRadio = (RadioButton)findViewById(R.id.radioButton2);
        femaleRadio.setOnClickListener(this);

        selected_date = (TextView)findViewById(R.id.selected_date);
        selected_date.setOnClickListener(this);

        calendar = (ImageView)findViewById(R.id.calendar);
        calendar.setOnClickListener(this);


        SignUP_Btn = (Button)findViewById(R.id.SignUPBtn);
        SignUP_Btn.setOnClickListener(this);

        Personal_Layout = (LinearLayout)findViewById(R.id.Personal_inform_Layout);

        // ------------EditText find
        password = (EditText)findViewById(R.id.input_PW);
        confirm_password = (EditText)findViewById(R.id.input_pw);
        Fname = (EditText)findViewById(R.id.input_firstname);
        Lname = (EditText)findViewById(R.id.input_lastname);
        phone_num = (EditText)findViewById(R.id.input_phone);

        E_mailText = (TextView)findViewById(R.id.Emailtext);
        Intent intent = getIntent();

        User_email = intent.getStringExtra("Email");
        E_mailText.setText(User_email);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.radioButton:
                maleRadio.setChecked(true);
                femaleRadio.setChecked(false);
                gender = 1; // Male
                break;

            case R.id.radioButton2:
                femaleRadio.setChecked(true);
                maleRadio.setChecked(false);
                gender = 0; // Female
                break;

            case R.id.selected_date:
            case R.id.calendar:
                OnDateSetListener callback = new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        selected_date.setText(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                    }
                };
                datePickerDialog = new DatePickerDialog(view.getContext(), android.R.style.Theme_Material_Light_Dialog_Alert, callback, 2018, 0, 19);
                datePickerDialog.show();
                break;

            case R.id.Send_Email_btn:
                //Dialog  // 중복 확인 후 이메일 보내기.
                new AlertDialog.Builder(SignUPActivity.this)
                        .setTitle("E-mail Verification")
                        .setMessage("verification code sent to e-mail. Please enter it in 3 minutes.")
                        .setNegativeButton("Admit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Verification_Layout.setVisibility(View.VISIBLE);
                            }
                        })
                        .show();
                //
                break;

            case R.id.admitBtn:
                //Dialog match

                new AlertDialog.Builder(SignUPActivity.this)
                        .setTitle("Verification code")
                        .setMessage("The Verification code is correct")
                        .setNegativeButton("Admit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Personal_Layout.setVisibility(View.VISIBLE);
                            }
                        })
                        .show();
                break;

            case R.id.SignUPBtn:
                String id = User_email;
                String pw = password.getText().toString();
                String confirm_pw = confirm_password.getText().toString();
                String fname = Fname.getText().toString();
                String lname = Lname.getText().toString();
                String date = selected_date.getText().toString();
                String phone = phone_num.getText().toString();
                String Sgender = String.valueOf(gender);
                // PhoneNum Format change
                String phonenum = null;

                if(id.equals("") || pw.equals("") || confirm_pw.equals("") || fname.equals("") || lname.equals("") || date.equals("Select Date") || phone.equals("")) {
                    Toast.makeText(this, "please confirm blank", Toast.LENGTH_SHORT).show();
                }   else    {
                    if(id.matches("^[_0-9a-zA-Z-]+@[0-9a-zA-Z-]+(.[_0-9a-zA-Z-]+)*$")) {
                        if(date.equals("Select Date"))   {
                            Toast.makeText(this, "Confirm your birth", Toast.LENGTH_SHORT).show();
                        }   else    {
                            if (pw.equals(confirm_pw)) {
                                if (pw.matches(".*[0-9].*") && pw.matches(".*[a-z].*") && pw.length() >= 7) {
                                    if(phone.length() == 10) {
                                        StringBuilder phoneBuilder = new StringBuilder(phone);
                                        phoneBuilder.insert(3, "-");
                                        phoneBuilder.insert(7, "-");
                                        phonenum = String.valueOf(phoneBuilder);

                                        signup.signup_Asycn(id, pw, fname, lname, Sgender, date, phonenum);
                                        Toast.makeText(this, "Sign Up Success\nLogin Please", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }   else    {
                                        Toast.makeText(this, "Phone number is wrong", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "Password rule is wrong", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Confirm password is wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }   else    {
                        Toast.makeText(this, "E-mail password is wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}



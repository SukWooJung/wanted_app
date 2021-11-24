package org.techtown.wanted_app_main.Activity.Login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;
import org.techtown.wanted_app_main.Adapter.PostingAdapter;
import org.techtown.wanted_app_main.R;
import org.techtown.wanted_app_main.ServerRequest.GetPersonalsRequest;
import org.techtown.wanted_app_main.database.Dto.PostingDtoInPersonal;
import org.techtown.wanted_app_main.database.OuterApi.OuterData;
import org.techtown.wanted_app_main.database.OuterApi.School;
import org.techtown.wanted_app_main.database.Personal;
import org.techtown.wanted_app_main.database.Posting;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginRegisterActivity extends AppCompatActivity {

    //성별
    Spinner spinner_gender;
    Integer value_gender;
    //학년
    Spinner spinner_grade;
    Integer value_grade;
    //나이
    Spinner spinner_age;
    Integer value_age;
    //비밀번호+
    EditText et_pwd, et_pwdcheck;
    TextView pwd_checkmes;
    //아이디
    EditText et_id;
    Button id_dupcheck;
    TextView id_checkmes;
    //이미지
    CheckBox r1, r2, r3, r4, r5, r6;
    String imageName;
    //닉네임
    EditText et_nickname;
    //역량
    EditText et_career;
    //등록
    Button post_person;
    //Dialog
    Dialog dialog;
    //학교
    EditText et_school;
    //학과
    EditText et_major;

    Button btnSchoolSearch, btnMajorSearch;

    boolean idValidation = false;
    boolean pwValidation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        //성별,사는곳,학교,학과,학년 spinner 지정
        spinner_gender = findViewById(R.id.register_gender_spinner);
        setSpinner("gender");
        spinner_grade = findViewById(R.id.register_grade_spinner);
        setSpinner("grade");
        spinner_age = findViewById(R.id.register_age_spinner);
        setSpinner("age");

        et_school = findViewById(R.id.register_school);

        btnSchoolSearch = findViewById(R.id.register_school_search);
        btnSchoolSearch.setOnClickListener(v -> {

            AlertDialog.Builder alert = new AlertDialog.Builder(LoginRegisterActivity.this);
            View dialogSchool = getLayoutInflater().inflate(R.layout.dialog_register_search, null);
            alert.setView(dialogSchool);

            AlertDialog alertDialog = alert.create();
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            RecyclerView recyclerView = alertDialog.findViewById(R.id.recyclerView_search);

//            PostingAdapter postingAdapter = new PostingAdapter();
//            recyclerView.setAdapter(postingAdapter);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));

//            for(String school : OuterData.schoolList) {
//                Items.add();
//            }

            alertDialog.show();

            EditText input = (EditText) dialogSchool.findViewById(R.id.search_school_edittext);
            Button btnSearchDB = (Button) dialogSchool.findViewById(R.id.search_school_btn);

            btnSearchDB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Items.clear();  //기존 리싸이클러뷰 초기화
                    String temp = input.getText().toString();
                    for (String school : OuterData.schoolList) {
                        if (school.contains(temp)) {
//                            Items.add();
                        }
                    }
                }
            });

            // + 리싸이클러뷰에서 하나의 뷰 클릭 시 해당 정보를 기존 LoginRegisterActicity -> (et_school) 칸 변경
        });


        //id
        et_id = findViewById(R.id.register_id);
        id_checkmes = findViewById(R.id.register_id_check_txt);
        id_dupcheck = findViewById(R.id.register_id_check_btn);
        id_dupcheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_id.getText().equals("")) {
                    id_checkmes.setText("아이디를 입력해주세요.");
                    return;
                }
                checkId();     //id중복확인
            }
        });


        //비밀번호
        et_pwd = findViewById(R.id.register_pwd);
        et_pwdcheck = findViewById(R.id.register_pwdcheck);
        pwd_checkmes = findViewById(R.id.register_pwdcheck_txt);

        et_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        et_pwdcheck.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        checkPwd();  //비밀번호확인: 비밀번호 재입력창 비밀번호랑 다를경우

        //이미지
        r1 = findViewById(R.id.iv1);
        r2 = findViewById(R.id.iv2);
        r3 = findViewById(R.id.iv3);
        r4 = findViewById(R.id.iv4);
        r5 = findViewById(R.id.iv5);
        r6 = findViewById(R.id.iv6);
        r1.setChecked(true);
        imageName = "profile_basic1";//이미지 기본설정
        selectMyImage(); //이미지 선택

        //닉네임
        et_nickname = findViewById(R.id.register_nickname);

        //학과
        btnMajorSearch = findViewById(R.id.profile_edit_major_search);

        //역량
        et_career = findViewById(R.id.register_career);

        //동록 버튼 클릭시
        post_person = findViewById(R.id.resup);
        post_person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postid = et_id.getText().toString();
                String postpwd = et_pwd.getText().toString();
                String postnickname = et_nickname.getText().toString();
                String postcareer = et_career.getText().toString();
                Integer postgender = value_gender;
                Integer postgrade = value_grade;
                Integer postage = value_age;
                String postimage = imageName; //선택된 이미지url 가져오기
                //Integer postage = Integer.valueOf(et_age.getText().toString());

                //제대로 입력안했을 시
                if ((postid.length() <= 0) || (postpwd.length() <= 0) || (postnickname.length() <= 0)
                        || !idValidation || !pwValidation) {
                    dialog = new Dialog(LoginRegisterActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_register);
                    TextView textView = dialog.findViewById(R.id.text);
                    Button cancel1 = dialog.findViewById(R.id.btnCancel);
                    cancel1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });

                    if (postid.length() == 0) {
                        textView.setText("아이디를 입력해주세요");
                        dialog.show();
                        return;
                    }
                    if (postpwd.length() == 0 ){
                        textView.setText("비밀번호를 입력해주세요");
                        dialog.show();
                        return;
                    }
                    if (postnickname.length() == 0 ){
                        textView.setText("닉네임을 입력해주세요");
                        dialog.show();
                        return;
                    }
                    if (!idValidation) {
                        textView.setText("아이디 중복 체크해주세요");
                        dialog.show();
                        return;
                    }
                    if (!pwValidation) {
                        textView.setText("비밀번호 중복 체크해주세요");
                        dialog.show();
                        return;
                    }

                } else {  //제대로 입력했을 시 서버에 post 후 LoginActivity로 이동

                    String url = "http://13.125.214.178:8080/personal";
                    Map map = new HashMap();
                    map.put("stringId", postid);
                    map.put("pwd", postpwd);
                    map.put("nickname", postnickname);
                    map.put("img", postimage);
                    map.put("school", "경기대"); // 바꿔야됨
                    map.put("major", "경영"); // 바꿔야됨
                    map.put("grade", postgrade);
                    map.put("career", postcareer);
                    map.put("age", postage);
                    map.put("gender", postgender);
                    map.put("address", "성남시"); // 바꿔야됨

                    JSONObject params = new JSONObject(map);

                    JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject obj) {
                                    System.out.println("회원가입 성공");
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    System.out.println("회원가입 실패");
                                    Log.e("register_Error", error.getMessage());
                                }
                            }) {

                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=UTF-8";
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                    queue.add(objectRequest);
                }

            }
        });


    }


    public void setSpinner(String topic) { //spinner 설정 함수

        //topic에 따른 spinner설정
        //성별
        if (topic.equals("gender")) {
            ArrayAdapter genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_dropdown_item);
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_gender.setAdapter(genderAdapter);

            spinner_gender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    value_gender = position;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        //학년
        else if (topic.equals("grade")) {
            ArrayAdapter genderAdapter = ArrayAdapter.createFromResource(this, R.array.grade, android.R.layout.simple_spinner_dropdown_item);
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_grade.setAdapter(genderAdapter);

            spinner_grade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    value_grade = position + 1;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
        //나이
        else if (topic.equals("age")) {
            ArrayAdapter genderAdapter = ArrayAdapter.createFromResource(this, R.array.age, android.R.layout.simple_spinner_dropdown_item);
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner_age.setAdapter(genderAdapter);

            spinner_age.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    value_age = position + 19;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        }
    }

    public void checkId() { //아이디 중복검사

        RequestQueue requestQueue;
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();

        String url1 = "http://13.125.214.178:8080/personal/stringId/" + et_id.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                id_checkmes.setText("사용 가능한 아이디입니다.");
                idValidation = true;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                id_checkmes.setText("이미 아이디가 존재합니다.");
                idValidation = false;
            }
        });
        requestQueue.add(stringRequest);
    }


    public void checkPwd() { //비밀번호와 비밀번호확인 일치체크
        et_pwdcheck.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_pwdcheck.isFocusable() && s.toString().equals(et_pwd.getText().toString())) {
                    pwd_checkmes.setText("비밀번호를 올바르게 입력했습니다.");
                    pwValidation = true;
                } else {
                    pwd_checkmes.setText("비밀번호를 다시 확인해주세요.");
                    pwValidation = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void selectMyImage() {  //이미지 선택
        r1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    r2.setChecked(false);
                    r3.setChecked(false);
                    r4.setChecked(false);
                    r5.setChecked(false);
                    r6.setChecked(false);
                    imageName = "profile_basic1";
                }
            }
        });
        r2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    r1.setChecked(false);
                    r3.setChecked(false);
                    r4.setChecked(false);
                    r5.setChecked(false);
                    r6.setChecked(false);
                    imageName = "profile_basic2";
                }
            }
        });
        r3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    r1.setChecked(false);
                    r2.setChecked(false);
                    r4.setChecked(false);
                    r5.setChecked(false);
                    r6.setChecked(false);
                    imageName = "profile_basic3";
                }
            }
        });
        r4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    r1.setChecked(false);
                    r2.setChecked(false);
                    r3.setChecked(false);
                    r5.setChecked(false);
                    r6.setChecked(false);
                    imageName = "profile_basic4";
                }
            }
        });
        r5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    r1.setChecked(false);
                    r2.setChecked(false);
                    r3.setChecked(false);
                    r4.setChecked(false);
                    r6.setChecked(false);
                    imageName = "profile_basic5";
                }
            }
        });
        r6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    r1.setChecked(false);
                    r2.setChecked(false);
                    r3.setChecked(false);
                    r4.setChecked(false);
                    r5.setChecked(false);
                    imageName = "profile_basic6";
                }
            }
        });
    }
}
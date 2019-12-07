package com.example.a02.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import android.os.Bundle;

import com.example.a02.InputActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.PersistableBundle;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a02.CalendarDb;
import com.example.a02.MainAdapter;
import com.example.a02.MainData;
import com.example.a02.InputActivity;


import com.example.a02.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ArrayList<MainData> arrayList;
    MainAdapter mainAdapter;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    CalendarDb calendarDb;

    // 임시로 데이터를 받을 변수들
    String tmp_content;
    int tmp_gns;
    int tmp_gne;
    String tmp_sound;
    String tmp_picture;
    int tmp_period;



    public String currDate()
    {
        long now = System.currentTimeMillis();
        Date mDate = new Date(now);
        SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDate.format(mDate);

        return date;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });







        recyclerView = (RecyclerView) getView().findViewById(R.id.recycler1);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        calendarDb = new CalendarDb(getActivity(), "mydb");
        arrayList = new ArrayList<>();
        mainAdapter = new MainAdapter(arrayList);

        recyclerView.setAdapter(mainAdapter);
        mainAdapter.notifyDataSetChanged();


        String date = currDate();
        // db에 있는 오늘 날짜의 데이터 가져오기
        ArrayList dbData = calendarDb.selectALLTABLE(date);

        // mainData 형식으로 변환하여 arrayList에 추가
        int i = 0;
        while (i * 6 < dbData.size()) {
            tmp_content = (String) dbData.get(1 + 6 * i);
            tmp_gns = (int) dbData.get(2 + 6 * i);
            tmp_gne = (int) dbData.get(3 + 6 * i);
            tmp_sound = (String) dbData.get(4 + 6 * i);
            tmp_picture = (String) dbData.get(5 + 6 * i);
            tmp_period = (int) dbData.get(6 + 6 * i);

            MainData mainData = new MainData(tmp_content, tmp_gns, tmp_gne, tmp_sound, tmp_picture, tmp_period);
            arrayList.add(mainData);
        }


        // 임시 일정추가 버튼
        Button btn_add = (Button) getView().findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent intent = new Intent(
                        getActivity(),
                        InputActivity.class
                );
                startActivityForResult(intent, 1001);
            }
        });



        return root;
    }
    // 메인 액티비티로 돌아왔을때 실행
    public void onRestart(){
        this.onRestart();

        // input data
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("input", 0);

        String date = currDate();
        tmp_content = sharedPreferences.getString("content", "");
        tmp_gns = sharedPreferences.getInt("gns", 0);
        tmp_gne = sharedPreferences.getInt("gne", 0);
        tmp_sound = sharedPreferences.getString("sound", "");
        tmp_picture = sharedPreferences.getString("picture", "");
        tmp_period = sharedPreferences.getInt("period", 0);

        // content가 정상적일떄 db에 insert , arrayList에 maindata로 추가
        if (!tmp_content.equals("")) {
            calendarDb.InsertTABLE(date, tmp_content, tmp_gns, tmp_gne, tmp_sound, tmp_picture, tmp_period);

            MainData mainData = new MainData(tmp_content, tmp_gns, tmp_gne, tmp_sound, tmp_picture, tmp_period);
            arrayList.add(mainData);

            mainAdapter.notifyDataSetChanged();
        }

        // sharedPreferences 값 초기화
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("content").commit();
        editor.remove("gns").commit();
        editor.remove("gne").commit();
        editor.remove("sound").commit();
        editor.remove("picture").commit();
        editor.remove("period").commit();
    }
}
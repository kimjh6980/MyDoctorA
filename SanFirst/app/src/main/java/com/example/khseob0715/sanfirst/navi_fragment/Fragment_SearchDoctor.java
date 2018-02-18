package com.example.khseob0715.sanfirst.navi_fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.khseob0715.sanfirst.R;
import com.example.khseob0715.sanfirst.ServerConn.SearchList;
import com.example.khseob0715.sanfirst.ServerConn.showConnectList;

import static com.example.khseob0715.sanfirst.UserActivity.UserActivity.usn;


/**
 * A simple {@link Fragment} subclass.
 */

public class Fragment_SearchDoctor extends Fragment {

    private ListView listView, listView2,listView3;

    private myAdapter adapter;
    private myAdapter2 adapter2;
    private myAdapter3 adapter3;

    private String[] items = new String[10];

    private ViewGroup rootView;

    int searchmethod = 0;

    private Handler handler;

    public static int search_count = 0;
    public static String[] Search_fname= new String[100];
    public static String[] Search_lname = new String[100];
    public static String[] Search_ID = new String[100];
    public static String[] Search_Gender = new String[100];
    public static String[] Search_old = new String[100];

    private EditText searchName2, searchEmail2;

    private Button searchDoctorBtn,ALLBtn;
    SearchList searchlist = new SearchList();
    showConnectList showconlist = new showConnectList();

    public Fragment_SearchDoctor() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup)inflater.inflate(R.layout.fragment_search_doctor, container, false);

        listView = (ListView) rootView.findViewById(R.id.DoctorList);
        adapter = new myAdapter();
        listView.setAdapter(adapter);

        listView2 = (ListView)rootView.findViewById(R.id.DoctorList2);
        adapter2 = new myAdapter2();
        listView2.setAdapter(adapter2);

        listView3 = (ListView)rootView.findViewById(R.id.DoctorRequestList);
        adapter3 = new myAdapter3();
        listView3.setAdapter(adapter3);

        searchName2 = (EditText)rootView.findViewById(R.id.SearchName2);
        searchEmail2 = (EditText)rootView.findViewById(R.id.SearchEmail2);
        searchEmail2.setVisibility(View.GONE);

        Spinner spinner2 = (Spinner)rootView.findViewById(R.id.spinner2);

        ArrayAdapter Spinner_adapter2 = ArrayAdapter.createFromResource(getContext(), R.array.search, R.layout.spinner_item);
        spinner2.setAdapter(Spinner_adapter2);

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l){

                if(position == 0){
                    searchmethod = 0;
                    searchName2.setVisibility(View.VISIBLE);
                    searchEmail2.setVisibility(View.GONE);
                }else{
                    searchmethod = 1;
                    searchName2.setVisibility(View.GONE);
                    searchEmail2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        TabHost host = (TabHost)rootView.findViewById(R.id.DoctorTabHost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("tab1");
        //spec.setIndicator("My Doctor");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.doctortext, null));
        spec.setContent(R.id.tab1);
        host.addTab(spec);

        spec = host.newTabSpec("tab2");
        //spec.setIndicator("Search");
        spec.setIndicator(null, ResourcesCompat.getDrawable(getResources(), R.drawable.searchtext, null));
        spec.setContent(R.id.tab2);
        host.addTab(spec);

        for(int i=0;i<host.getTabWidget().getChildCount();i++){
            TextView tv = (TextView) host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#000000"));
        }

        searchDoctorBtn = (Button) rootView.findViewById(R.id.SearchdisconDoctor);
        searchDoctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = null;
                String value = null;
                if(searchmethod == 0)    {
                    type = "name";
                    value = searchName2.getText().toString();
                }   else if (searchmethod == 1) {
                    type = "email";
                    value = searchEmail2.getText().toString();
                }
                searchlist.SearchList_Asycn(1, type, value);
                handler.postDelayed(new Update_list(),1000);

            }
        });

        ALLBtn = (Button)rootView.findViewById(R.id.AllDoctor);
        ALLBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchlist.SearchList_Asycn(1);
                handler.postDelayed(new Update_list(),1000);
            }
        });

        handler = new Handler();

        showconlist.showConnectList_Asycn(1, usn);
        searchlist.SearchList_Asycn(1);
        handler.postDelayed(new Update_list(),1000);

        return rootView;
    }

    class myAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return search_count;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SearchDoctor view = new SearchDoctor(rootView.getContext());
            final LinearLayout detail_layout = (LinearLayout)view.findViewById(R.id.DetailLayout);

            Button Detail = (Button)view.findViewById(R.id.list1DetailBtn);
            Detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detail_layout.setVisibility(View.VISIBLE);
                }
            });

            Button Disconnect = (Button)view.findViewById(R.id.Disconnect);
            Disconnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


            return view;
        }
    }

    class SearchDoctor extends LinearLayout {
        TextView Test;

        public SearchDoctor(Context context) {
            super(context);
            init(context);
        }

        public SearchDoctor(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        private void init(Context context) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.search_doctor_list, this);
        }

    }


    class myAdapter2 extends BaseAdapter {

        @Override
        public int getCount() {
            return search_count;
        }

        @Override
        public Object getItem(int position) {
            return Search_fname[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SearchDoctor2 view = new SearchDoctor2(rootView.getContext());

            final LinearLayout detail_layout2 = (LinearLayout)view.findViewById(R.id.DetailLayout2);

            Button Detail = (Button)view.findViewById(R.id.list2DetailBtn);
            Detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detail_layout2.setVisibility(View.VISIBLE);
                }
            });

            Button Connection = (Button)view.findViewById(R.id.Connect);
            Connection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            TextView FirstName = (TextView)view.findViewById(R.id.List2FirstName);
            TextView LastName = (TextView)view.findViewById(R.id.List2LastName);
            TextView DoctorEmail = (TextView)view.findViewById(R.id.List2DoctorEmailText);
            TextView Gender = (TextView)view.findViewById(R.id.List2DoctorGenderText);
            TextView Old = (TextView)view.findViewById(R.id.List2DoctorOldText);

            FirstName.setText(Search_fname[position]);
            LastName.setText(Search_lname[position]);
            DoctorEmail.setText(Search_ID[position]);
            Gender.setText(Search_Gender[position]);
            Old.setText(Search_old[position]);

            return view;
        }
    }

    class SearchDoctor2 extends LinearLayout {
        TextView Test;

        public SearchDoctor2(Context context) {
            super(context);
            init(context);
        }

        public SearchDoctor2(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        private void init(Context context) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.search_doctor_list2, this);
        }

    }


    class myAdapter3 extends BaseAdapter {

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SearchDoctor3 view = new SearchDoctor3(rootView.getContext());

            final LinearLayout detail_layout2 = (LinearLayout)view.findViewById(R.id.DetailLayout2);

            Button Detail = (Button)view.findViewById(R.id.list3DetailBtn);
            Detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    detail_layout2.setVisibility(View.VISIBLE);
                }
            });

            Button Acceptance = (Button)view.findViewById(R.id.Acceptance);
            Acceptance.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


            return view;
        }
    }

    class SearchDoctor3 extends LinearLayout {
        TextView Test;

        public SearchDoctor3(Context context) {
            super(context);
            init(context);
        }

        public SearchDoctor3(Context context, @Nullable AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        private void init(Context context) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.search_doctor_list3, this);
        }

    }

    private class Update_list implements Runnable{

        @Override
        public void run() {
            adapter2.notifyDataSetChanged();
        }
    }
}

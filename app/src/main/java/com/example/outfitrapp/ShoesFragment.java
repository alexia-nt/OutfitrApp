package com.example.outfitrapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoesFragment extends Fragment implements MyAdapter.OnItemClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ShoesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShoesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShoesFragment newInstance(String param1, String param2) {
        ShoesFragment fragment = new ShoesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shoes, container, false);
    }

    private FirebaseStorage mStorage;
    private ValueEventListener valueEventListener;
    FloatingActionButton fab;
    private RecyclerView recyclerView;
    private ArrayList<DataClass> dataList;
    private MyAdapter adapter;
    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
    String userId = currentUser.getUid();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    // Get the reference to the current user's node
    DatabaseReference userRef = database.getReference("Users").child(userId);

    // Create a new node inside the current user's node
    DatabaseReference databaseReference = userRef.child("ShoesSlider");
    //final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("TopsSlider");

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fab = view.findViewById(R.id.fab);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(this.getActivity(),2));
        dataList = new ArrayList<>();
        adapter = new MyAdapter(this.getActivity(), dataList);
        recyclerView.setAdapter(adapter);


        adapter.setOnItemClickListener(ShoesFragment.this);

        mStorage=FirebaseStorage.getInstance();
        valueEventListener= databaseReference.addValueEventListener(new ValueEventListener() {
            /*
             * Display of users' photos. Images are sorted using RecyclerView. It shows the shoes
             * that users have chosen to have in their wardrobe. The photos are displayed in the
             * order selected by the user and with the description.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    dataClass.setKey(dataSnapshot.getKey());
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        /*
         * Enables by pressing the “+” button located at the bottom right to
         * be transferred to the Upload Shoes Activity to insert a new image.
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),UploadShoesActivity.class);
                startActivity(intent);
                /* getActivity().finish();*/
            }
        });
    }

    /*
     * Can delete a photo by long pressing on it, so we get the position
     * and key of the corresponding image and remove it from firebase. If
     * the deletion is successful, it displays the message “Image deleted”.
     */
    @Override
    public void onDeleteClick(int position) {
        DataClass selected=dataList.get(position);
        String selectedKey=selected.getKey();
        StorageReference img=mStorage.getReferenceFromUrl(selected.getImageURL());
        img.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                databaseReference.child(selectedKey).removeValue();
                Toast.makeText(getActivity(),"Image deleted",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }

    @Override
    public void onItemClick(int position) {

    }

}
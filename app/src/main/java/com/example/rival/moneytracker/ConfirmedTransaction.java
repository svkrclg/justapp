package com.example.rival.moneytracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConfirmedTransaction.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConfirmedTransaction#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmedTransaction extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String TAG="ConfirmedTransaction";
    private OnFragmentInteractionListener mListener;
    private RecyclerView mConfirmTransactionRView;
    private ArrayList<ConfTranClass> mArraylist=new ArrayList<>();
    private ConfTranCardAdapter mAdapter;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String uid;

    ProgressBar progressBar;
    TextView tv;
    public ConfirmedTransaction() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfirmedTransaction.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfirmedTransaction newInstance(String param1, String param2) {
        ConfirmedTransaction fragment = new ConfirmedTransaction();
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
        View view= inflater.inflate(R.layout.fragment_confirmed_transaction, container, false);
        mConfirmTransactionRView=(RecyclerView) view.findViewById(R.id.ConfirmTransactionrecyclerView);
        progressBar=(ProgressBar) view.findViewById(R.id.progressBar);
        tv=(TextView) view.findViewById(R.id.notFound);
        mAdapter=new ConfTranCardAdapter(getContext(), mArraylist);
        mConfirmTransactionRView.setLayoutManager(new LinearLayoutManager(getContext()));
        mConfirmTransactionRView.setItemAnimator( new DefaultItemAnimator());
        mConfirmTransactionRView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mConfirmTransactionRView.setAdapter(mAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        uid=firebaseAuth.getUid();
        databaseReference=firebaseDatabase.getReference();
        databaseReference.child("users").child(uid).child("myTransactions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);
                if(dataSnapshot.getChildrenCount()==0)
                {
                    tv.setVisibility(View.VISIBLE);
                    Log.d(TAG, "wtF: "+dataSnapshot.getChildrenCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        loadData();
        return view;
    }
    int i=0;
    ArrayList<String> storeUidIndex=new ArrayList<>();
    private void loadData()
    {

        databaseReference.child("users").child(uid).child("myTransactions").addChildEventListener(new ChildEventListener() {
             @Override
             public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String s) {
                     tv.setVisibility(View.GONE);
                     final String oppnuid=ds.getKey().toString();
                     Log.d(TAG, "2"+oppnuid);
                     Log.d(TAG, "Children"+ds.toString());
                     final int netTotal=ds.child("netTotal").getValue(Integer.class);
                     databaseReference.child("userNameByUid").child(oppnuid).addValueEventListener(new ValueEventListener() {
                         int nt=netTotal;
                         String Ouid=oppnuid;
                         @Override
                         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                             String name=dataSnapshot.getValue(String.class);
                             String direction;
                             if(nt<0)
                                 direction="going";
                             else
                                 direction="coming";
                             mArraylist.add(new ConfTranClass(Math.abs(nt), Ouid, name, direction));
                             storeUidIndex.add(Ouid);
                             mAdapter.notifyDataSetChanged();
                         }

                         @Override
                         public void onCancelled(@NonNull DatabaseError databaseError) {

                         }
                     });



             }

             @Override
             public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                   Log.d(TAG, "change "+dataSnapshot.toString()+ "---" +s);
                   try {
                       int pos = storeUidIndex.indexOf(dataSnapshot.getKey());
                       ConfTranClass object = mArraylist.get(pos);
                       int nt = dataSnapshot.child("netTotal").getValue(Integer.class);
                       String direction = nt < 0 ? "going" : "coming";
                       ConfTranClass newObject = new ConfTranClass(Math.abs(nt), object.getOpponentUid(), object.getName(), direction);
                       mArraylist.set(pos, newObject);
                       mAdapter.notifyDataSetChanged();
                   }
                   catch (Exception e)
                   {
                       Log.d(TAG, "Yes exception: "+e.toString());
                   }
             }

             @Override
             public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                 int pos =storeUidIndex.indexOf(dataSnapshot.getKey());
                 storeUidIndex.remove(dataSnapshot.getKey());
                 mArraylist.remove(pos);
                 mAdapter.notifyDataSetChanged();
                 if(mArraylist.isEmpty())
                     tv.setVisibility(View.VISIBLE);

             }

             @Override
             public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                 Log.d(TAG, "moved "+dataSnapshot.toString()+ "---" +s);

             }

             @Override
             public void onCancelled(@NonNull DatabaseError databaseError) {

             }
         });
    }
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

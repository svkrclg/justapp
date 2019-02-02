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
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PendingTransaction.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PendingTransaction#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingTransaction extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private RecyclerView mPendingTransactionRView;
    private ArrayList<PendTranClass>  mArraylist=new ArrayList<>();
    private PendTranCardAdapter mAdapter;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String uid;
    ProgressBar progressBar;
    TextView tv;
    public PendingTransaction() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PendingTransaction.
     */
    // TODO: Rename and change types and number of parameters
    public static PendingTransaction newInstance(String param1, String param2) {
        PendingTransaction fragment = new PendingTransaction();
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
        View view =inflater.inflate(R.layout.fragment_pending_transaction, container, false);
        mPendingTransactionRView=(RecyclerView) view.findViewById(R.id.pendingTransactionrecyclerView);
        mAdapter=new PendTranCardAdapter(getContext(), mArraylist);
        mPendingTransactionRView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPendingTransactionRView.setItemAnimator( new DefaultItemAnimator());
        mPendingTransactionRView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        mPendingTransactionRView.setAdapter(mAdapter);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        uid=firebaseAuth.getUid();
        databaseReference=firebaseDatabase.getReference();
        progressBar=(ProgressBar) view.findViewById(R.id.progressBar);
        tv=(TextView) view.findViewById(R.id.notFound);
        databaseReference.child("users").child(uid).child("pendingTransactions").addValueEventListener(new ValueEventListener() {
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
   Boolean isMyAdded;
   int index=0;
   ArrayList<String> storeIndex =new ArrayList<>();
    public void loadData(){
        databaseReference.child("users").child(uid).child("pendingTransactions").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                tv.setVisibility(View.GONE);
                     final String keytimeMillis=dataSnapshot.getKey();
                     String Tuid[]=new String[2];
                     Boolean status[]= new Boolean[2];
                     int i=0;
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                     Tuid[i]=ds.getKey();
                     status[i]=ds.getValue(Boolean.class);
                     i++;
                }
                isMyAdded=false;
                if(Tuid[0].equals(uid))
                {
                    //Added By me
                    if(status[0]==true)
                    {
                        isMyAdded=true;
                        Log.d("PendingTransaction ", i+" 1");
                    }
                }
                if(Tuid[1].equals(uid))
                {
                    //Added By me
                    if(status[1]==true)
                    {
                        isMyAdded=true;
                        Log.d("PendingTransaction ", i+" 2");
                    }
                }
                //GetOpponet uid
                final String opponetUid;
                if(Tuid[0].equals(uid))
                    opponetUid=Tuid[1];
                else
                    opponetUid=Tuid[0];
                //Get opponet name
                databaseReference.child("userNameByUid").child(opponetUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    boolean finalIsMyAdded=isMyAdded;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String Opponetname=dataSnapshot.getValue(String.class);
                        //Get reason
                        databaseReference.child("transactions").child(keytimeMillis).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String reason=dataSnapshot.child("reason").getValue(String.class);
                                int amount =dataSnapshot.child("amount").getValue(Integer.class);
                                String toUid=dataSnapshot.child("to").getValue(String.class);
                                String fromUid=dataSnapshot.child("from").getValue(String.class);
                                String addedUid=dataSnapshot.child("addedBy").getValue(String.class);
                                String dir;
                                if(toUid.equals(uid))
                                    dir="coming";
                                else
                                    dir="going";
                                Log.d(TAG, "key: "+keytimeMillis +" index: "+index);
                                storeIndex.add(keytimeMillis);
                                index++;
                                mArraylist.add(new PendTranClass(finalIsMyAdded, amount, reason+"\n"+dir, opponetUid, Opponetname, Long.parseLong(keytimeMillis)));
                                mAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                index--;
                String key=dataSnapshot.getKey();
                int pos= storeIndex.indexOf(key);
                storeIndex.remove(key);
                mArraylist.remove(pos);
                mAdapter.notifyDataSetChanged();
                Log.d(TAG, "IndexTracking: "+index);
                if(mArraylist.isEmpty())
                    tv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

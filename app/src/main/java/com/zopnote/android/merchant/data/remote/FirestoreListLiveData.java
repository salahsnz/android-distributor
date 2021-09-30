package com.zopnote.android.merchant.data.remote;

import android.arch.lifecycle.LiveData;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * Created by nmohideen on 04/02/18.
 */

public class FirestoreListLiveData<T> extends LiveData<List<T>> {

    private Query query;
    private Class<T> typeParameterClass;
    private ListenerRegistration listenerRegistration;

    public FirestoreListLiveData(Query query, Class typeParameterClass) {
        this.query = query;
        this.typeParameterClass = typeParameterClass;
    }

    private EventListener<QuerySnapshot> listener = new EventListener<QuerySnapshot>() {
        @Override
        public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
            if ( documentSnapshots != null) {
                List<T> items = documentSnapshots.toObjects(typeParameterClass);
                postValue(items);
            } else {
                // TODO
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onActive() {
        listenerRegistration = query.addSnapshotListener(listener);
    }

    @Override
    protected void onInactive() {
        listenerRegistration.remove();
    }
}

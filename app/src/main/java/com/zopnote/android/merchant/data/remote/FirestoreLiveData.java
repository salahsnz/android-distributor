package com.zopnote.android.merchant.data.remote;

import android.arch.lifecycle.LiveData;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

/**
 * Created by nmohideen on 04/02/18.
 */

public class FirestoreLiveData<T> extends LiveData<T> {

    private DocumentReference docRef;
    private Class<T> typeParameterClass;
    private ListenerRegistration listenerRegistration;

    public FirestoreLiveData(DocumentReference docRef, Class typeParameterClass) {
        this.docRef = docRef;
        this.typeParameterClass = typeParameterClass;
    }

    private EventListener<DocumentSnapshot> listener = new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
            if (documentSnapshot != null) {
                if (documentSnapshot.exists()) {
                    T object = documentSnapshot.toObject(typeParameterClass);
                    postValue(object);
                }
            } else {
                // TODO
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onActive() {
        listenerRegistration = docRef.addSnapshotListener(listener);
    }

    @Override
    protected void onInactive() {
        listenerRegistration.remove();
    }
}

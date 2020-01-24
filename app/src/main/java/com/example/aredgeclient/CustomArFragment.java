package com.example.aredgeclient;

import android.util.Log;

import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.sceneform.ux.ArFragment;

public class CustomArFragment extends ArFragment {
    @Override
    protected Config getSessionConfiguration(Session session){

        getPlaneDiscoveryController().setInstructionView(null);
//        getPlaneDiscoveryController().hide();
        Config config = new Config(session);
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
        session.configure(config);
        this.getArSceneView().setupSession(session);

        ((ArActivity)getActivity()).setUpAigmantedImageDB(config,session);



        return config;
    }

}
package com.example.aredgeclient;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.assets.RenderableSource;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ArActivity extends AppCompatActivity {
    private CustomArFragment arFragment;
    private Map<String, Bitmap>imageMap;
    private Set<String>placedImageSet;
    private Map<String,String>renderableIdMap,renderablePathMap;
    private static final String GLTF_ASSET =
            "https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        imageMap=ImageStorage.getImageMap();
        renderableIdMap=(HashMap<String,String>)getIntent().getSerializableExtra("renderableIdMap");
        renderablePathMap=(HashMap<String,String>)getIntent().getSerializableExtra("renderablePathMap");
        placedImageSet= new HashSet<>();
        Log.d("SETUP", "MAPS LOADED");


        arFragment= (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.sceneform_fragment);
        arFragment.getPlaneDiscoveryController().hide();
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdateFrame);
    }

    /**
     *
     * @param config config used by the ArFragment
     * @param session session with which the fragment is initialized
     */
    public void setUpAigmantedImageDB(Config config, Session session){
        AugmentedImageDatabase augmentedImageDatabase=new AugmentedImageDatabase(session);
        for(String key:imageMap.keySet()){
            augmentedImageDatabase.addImage(key,imageMap.get(key));
        }
        config.setAugmentedImageDatabase(augmentedImageDatabase);
    }

    /**
     * @param frameTime representation of what the phone currently sees
     */
    private void onUpdateFrame(FrameTime frameTime){
        Frame frame=arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> images=frame.getUpdatedTrackables(AugmentedImage.class);
        for(AugmentedImage image:images){
            if(image.getTrackingState()== TrackingState.TRACKING)
            {
                if(imageMap.containsKey(image.getName())&&!placedImageSet.contains(image.getName())){
                    Log.d("FOUND IMAGE","FOUND IMAGE");
//                    File file = new File(ArActivity.this.getFilesDir().toString()
//                            + "/DOWNLOAD.glb");
//                    Log.d("AR ACTIVITY FILE",""+ArActivity.this.getFilesDir().toString()
//                            + "/DOWNLOAD.glb"+file.exists());
                    placeObject(arFragment,image.createAnchor(image.getCenterPose()), Uri.parse(ArActivity.this.getFilesDir().toString()
                            + "/DOWNLOAD.glb"));
//                    placeObject(arFragment,image.createAnchor(image.getCenterPose()), Uri.parse("Duck.sfb"));
                    placedImageSet.add(image.getName());
                }
            }
        }
    }


    private void placeObject(ArFragment fragment, Anchor anchor, Uri model){
        Log.d("MODEL DOWNLOAD STARTED","Time: "+System.nanoTime());
        ModelRenderable.builder()
                .setSource(this, RenderableSource.builder().setSource(this,model,
                        RenderableSource.SourceType.GLB).setScale(0.02f).build()).setRegistryId(model).build()
                .thenAccept(renderable -> {
                    Log.d("MODEL DOWNLOAD DONE","Time: "+System.nanoTime());
                    addNodeToScene(fragment,anchor,renderable);})
                .exceptionally (
                        throwable -> {
                            Toast toast = Toast.makeText(this, "Unable to load model renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
//        ModelRenderable.builder().setSource(fragment.getContext(),model).build()
//                .thenAccept(renderable ->{
//                    Log.d("MODEL DOWNLOAD DONE","Time: "+System.nanoTime());
//                    addNodeToScene(fragment,anchor,renderable);}).exceptionally((throwable -> {
////            AlertDialog.Builder builder=new AlertDialog.Builder(myActivity);
////            builder.setMessage(throwable.getMessage()).setTitle("Error");
////            AlertDialog dialof=builder.create();
////            dialof.show();
//            Log.e("ERROR ADDING RENDERABLE","ERROR ADDING RENDERABLE");
//            return null;
//        }));
    }
    private void addNodeToScene(ArFragment fragnment, Anchor anchor, Renderable renderable){
        Log.d("ADDING NODE","FIRED");
        AnchorNode anchorNode=new AnchorNode(anchor);//Cannot be moved
        TransformableNode node=new TransformableNode(fragnment.getTransformationSystem());//can be scaled, rotated, moved
        node.setRenderable(renderable);
        node.setParent(anchorNode);
        fragnment.getArSceneView().getScene().addChild(anchorNode);//added to scene
        node.select();//selected the transformable node for object to be interactive
    }
}

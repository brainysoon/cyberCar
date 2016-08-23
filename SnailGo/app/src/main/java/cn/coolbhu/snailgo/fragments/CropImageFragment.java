package cn.coolbhu.snailgo.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.isseiaoki.simplecropview.util.Utils;

import java.io.File;

import cn.coolbhu.snailgo.MyApplication;
import cn.coolbhu.snailgo.R;
import cn.coolbhu.snailgo.activities.CropImageActivity;
import cn.coolbhu.snailgo.utils.IntentUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class CropImageFragment extends Fragment {
    private static final int REQUEST_PICK_IMAGE = 10011;
    private static final int REQUEST_SAF_PICK_IMAGE = 10012;
    private static final int REQUEST_PICK_PHOTO = 10013;

    //View
    private CropImageView mCropImageView;
    private Button mButtonSure;
    private Button mButtonCancle;
    private FloatingActionButton mActionOpen;
    private FloatingActionButton mActionPick;

    private View rootView;

    public CropImageFragment() {
        // Required empty public constructor
    }


    public static CropImageFragment newInstance() {
        CropImageFragment fragment = new CropImageFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_crop_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.rootView = view;

        initRootView(view);
    }

    //initRootView
    private void initRootView(View view) {

        findView(view);

        //setListener
        initCropImage();

        setListenerWithNoPermission();

        CropImageFragmentPermissionsDispatcher.setListenerWithCheck(this);
    }

    //Listener
    private void setListenerWithNoPermission() {

        //取消
        mButtonCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish();
            }
        });

        if (CropImageActivity.mCropCallback == null) {

            CropImageActivity.mCropCallback = new CropCallback() {
                @Override
                public void onSuccess(Bitmap cropped) {

                }

                @Override
                public void onError() {

                }
            };
        }

        //start crop
        mButtonSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mCropImageView.startCrop(createSaveUri(), CropImageActivity.mCropCallback, new SaveCallback() {
                    @Override
                    public void onSuccess(Uri outputUri) {

//                        MyApplication.mAvatorUri=outputUri;

                        if (getActivity()!=null){

                            getActivity().finish();
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        });
    }

    @NeedsPermission({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void setListener() {

        //openFile
        mActionOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //挑选图片
                pickImage();
            }
        });

        mActionPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickPhoto();
            }
        });

    }

    @OnShowRationale({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void showLoacationRationale(final PermissionRequest request) {

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.request_permission)
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.request_camera_file_permission)
                .setPositiveButton(R.string.allow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        request.proceed();
                    }
                })
                .setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        request.cancel();
                    }
                })

                .setCancelable(false)
                .show();
    }

    @OnPermissionDenied({Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
    public void showLoacationDenied() {

        Snackbar.make(rootView, R.string.permission_denie, Snackbar.LENGTH_LONG)
                .setAction(R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        IntentUtils.toSnailGoSettings(getContext());
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        CropImageFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    //Uri
    public Uri createSaveUri() {
        return Uri.fromFile(new File(MyApplication.USER_AVATOR_DIRCTORY));
    }

    //initCropImage
    private void initCropImage() {

        if (CropImageActivity.mAvator != null) {
            mCropImageView.setImageBitmap(CropImageActivity.mAvator);
        }

        mCropImageView.setCropMode(CropImageView.CropMode.CIRCLE);

        mCropImageView.setOutputWidth(200);
        mCropImageView.setOutputHeight(200);
    }

    //findView
    private void findView(View rootView) {

        mCropImageView = (CropImageView) rootView.findViewById(R.id.fragment_crop_image_cropImageView_crop);
        mButtonSure = (Button) rootView.findViewById(R.id.fragment_crop_image_button_sure);
        mButtonCancle = (Button) rootView.findViewById(R.id.fragment_crop_image_button_cancle);
        mActionOpen = (FloatingActionButton) rootView.findViewById(R.id.fragment_crop_image_action_open);
        mActionPick = (FloatingActionButton) rootView.findViewById(R.id.fragment_crop_image_action_pick);
    }

    //拍照
    public void pickPhoto() {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        startActivityForResult(intent, REQUEST_PICK_PHOTO);
    }

    public void pickImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), REQUEST_PICK_IMAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_SAF_PICK_IMAGE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
//            showProgress();
            mCropImageView.startLoad(result.getData(), mLoadCallback);
        } else if (requestCode == REQUEST_SAF_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
//            showProgress();
            mCropImageView.startLoad(Utils.ensureUriPermission(getContext(), result), mLoadCallback);
        } else if (requestCode == REQUEST_PICK_PHOTO && resultCode == Activity.RESULT_OK) {

            Bundle bundle = result.getExtras();

            Bitmap b = (Bitmap) bundle.get("data");

            mCropImageView.setImageBitmap(b);
        }
    }

    private final LoadCallback mLoadCallback = new LoadCallback() {
        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {

        }
    };
}

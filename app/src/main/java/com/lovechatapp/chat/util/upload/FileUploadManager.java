package com.lovechatapp.chat.util.upload;


import com.lovechatapp.chat.base.AppManager;
import com.lovechatapp.chat.listener.OnCommonListener;
import com.lovechatapp.chat.util.ToastUtil;
import com.lovechatapp.chat.util.permission.PermissionUtil;

import java.util.List;

/**
 * 文件上传器
 */
public class FileUploadManager {

    private static void upload(final List<UploadTask> uploadTasks, final OnCommonListener<Boolean> onCommonListener) {

        if (uploadTasks != null && uploadTasks.size() > 0) {
            UploadTask task = uploadTasks.get(0);
            OnCommonListener<UploadTask> listener = new OnCommonListener<UploadTask>() {
                @Override
                public void execute(UploadTask task) {
                    if (!task.ok) {
                        ToastUtil.INSTANCE.showToast(task.message);
                        if (onCommonListener != null) {
                            onCommonListener.execute(false);
                        }
                    } else {
                        boolean allOk = true;
                        for (UploadTask uploadTask : uploadTasks) {
                            if (!uploadTask.ok) {
                                allOk = false;
                                executeTask(uploadTask, this);
                                break;
                            }
                        }
                        if (allOk) {
                            if (onCommonListener != null)
                                onCommonListener.execute(true);
                        }
                    }
                }
            };
            executeTask(task, listener);
        }
    }

    /**
     * 上传图片/视频文件集合
     */
    public static void permissionUpload(final List<UploadTask> uploadTasks, final OnCommonListener<Boolean> onCommonListener) {
        PermissionUtil.requestPermissions(AppManager.getInstance(), new PermissionUtil.OnPermissionListener() {
            @Override
            public void onPermissionGranted() {
                upload(uploadTasks, onCommonListener);
            }

            @Override
            public void onPermissionDenied() {
                ToastUtil.INSTANCE.showToast("没有文件读写权限，无法上传");
            }
        });
    }

    private static void executeTask(UploadTask task, OnCommonListener<UploadTask> listener) {
        if (task.isVideo) {
            new VideoUploader().execute(task, listener);
        } else {
            new PictureUploader().execute(task, listener);
        }
    }
}
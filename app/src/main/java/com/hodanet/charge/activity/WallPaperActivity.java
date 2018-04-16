package com.hodanet.charge.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.hodanet.charge.R;
import com.hodanet.charge.adapter.PicTypeViewPagerAdapter;
import com.hodanet.charge.adapter.WallpaperViewPagerAdapter;
import com.hodanet.charge.fragment.PicFragment;
import com.hodanet.charge.info.pic.WallpaperInfo;
import com.hodanet.charge.utils.TaskManager;
import com.hodanet.charge.utils.ToastUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WallPaperActivity extends BaseActivity {


    @BindView(R.id.vp_wallPaper)
    ViewPager vpWallPaper;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.img_download)
    ImageView imgDownload;
    private List<WallpaperInfo> list;
    private WallpaperViewPagerAdapter adapter;
    private List<String> savedImgUrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall_paper);
        ButterKnife.bind(this);

        initData();

        initView();

    }
    private void initData() {
        Intent intent = getIntent();
        list = new ArrayList<>();
        if (intent.hasExtra(PicFragment.INTENT_KEY_THEME_WALLPAPERS)) {
            String result = intent.getStringExtra(PicFragment.INTENT_KEY_THEME_WALLPAPERS);
            try {
                JSONArray objs = new JSONArray(result);
                for (int i = 0; i < objs.length(); i++) {
                    WallpaperInfo info = new WallpaperInfo();
                    JSONObject obj = objs.optJSONObject(i);
                    info.setId(obj.optInt("id"));
                    info.setVip(obj.optInt("vip"));
                    info.setName(obj.optString("name"));
                    info.setFavorites(obj.optInt("favorites"));
                    info.setPic(obj.optString("pic"));
                    list.add(info);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        adapter = new WallpaperViewPagerAdapter(this, list, new WallpaperViewPagerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (imgBack.isShown()) {
                    imgBack.setVisibility(View.GONE);
                    imgDownload.setVisibility(View.GONE);
                } else {
                    imgBack.setVisibility(View.VISIBLE);
                    imgDownload.setVisibility(View.VISIBLE);
                }
            }
        });

        vpWallPaper.setAdapter(adapter);

        vpWallPaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                imgBack.setVisibility(View.GONE);
                imgDownload.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.img_back,R.id.img_download})
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.img_back:
                finish();
                break;
            case R.id.img_download:
                TaskManager.getInstance().executorNewTask(new Runnable() {
                    @Override
                    public void run() {
                        String tem = list.get(vpWallPaper.getCurrentItem()).getPic();
                        if (!tem.startsWith("http")) {
                            tem = PicTypeViewPagerAdapter.WALLPAPER_HOST + tem;
                        }
                        String url = tem.replaceAll("webp", "jpg");

                        if(savedImgUrlList != null && savedImgUrlList.contains(url)){
                            ToastUtil.toast(WallPaperActivity.this, "该图片已保存");
                            return;
                        }

                        try {
                            Bitmap bitmap = Glide.with(WallPaperActivity.this).load(url).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
//                            String imgURL = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "", "");
//                            if (!TextUtils.isEmpty(imgURL)) {
//                                ToastUtil.toast(WallPaperActivity.this, "保存图片成功！");
//                                if(savedImgUrlList == null) savedImgUrlList = new ArrayList<String>();
//                                savedImgUrlList.add(url);
//                            }else{
//                                ToastUtil.toast(WallPaperActivity.this, "保存图片失败");
//                            }
                            saveImageToGallery(WallPaperActivity.this, bitmap, url);

                        } catch (InterruptedException e) {
                            ToastUtil.toast(WallPaperActivity.this, "保存图片失败");
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            ToastUtil.toast(WallPaperActivity.this, "保存图片失败");
                            e.printStackTrace();
                        }catch(Exception e){
                            ToastUtil.toast(WallPaperActivity.this, "保存图片失败");
                            e.printStackTrace();
                        }
                    }
                });

                break;
        }

    }

    public void saveImageToGallery(Context context, Bitmap bmp, String url) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            ToastUtil.toast(WallPaperActivity.this, "保存图片成功！");
            if(savedImgUrlList == null) savedImgUrlList = new ArrayList<String>();
            savedImgUrlList.add(url);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
            // 最后通知图库更新
            MediaScannerConnection.scanFile(WallPaperActivity.this, new String[]{file.getAbsolutePath()}, new String[]{"image/jpg"}, new MediaScannerConnection.OnScanCompletedListener() {
                @Override
                public void onScanCompleted(String path, Uri uri) {
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}

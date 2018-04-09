package com.hodanet.charge.adapter.hot;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hodanet.charge.R;
import com.hodanet.charge.info.hot.BeautifulGirlInfo;

import java.util.List;

/**
 * Created by June on 2016/8/3.
 */
public class BeautifilGirlAdapter extends PagerAdapter {
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    private Context context;
    private List<BeautifulGirlInfo> mList;
    private ViewPagerListener listener;

    public BeautifilGirlAdapter(Context context, List<BeautifulGirlInfo> mList, ViewPagerListener listener) {
        this.context = context;
        this.mList = mList;
        this.listener = listener;

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        RelativeLayout view = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.item_beautiful_girl, null, false);
        final ImageView image = (ImageView) view.findViewById(R.id.image);
//        final RelativeLayout arrow_left = (RelativeLayout) view.findViewById(R.id.arrow_left);
//        final RelativeLayout arrow_right = (RelativeLayout) view.findViewById(R.id.arrow_right);
//        final LinearLayout layout_set = (LinearLayout) view.findViewById(R.id.layout_wallpaper);
//        final LinearLayout layout_download = (LinearLayout) view.findViewById(R.id.layout_download);
//        final RelativeLayout layout_bottom = (RelativeLayout) view.findViewById(R.id.layout_bottom);
        String url = mList.get(position).getPic();
        Glide.with(context).load(url).placeholder(R.mipmap.img_news_default).diskCacheStrategy(DiskCacheStrategy.RESULT).into(image);
        image.setTag(position);
//        arrow_left.setVisibility(View.GONE);
//        arrow_right.setVisibility(View.GONE);
//        layout_bottom.setVisibility(View.GONE);
//        layout_set.setTag(url);
//        layout_download.setTag(url);
//        image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int location = (int) v.getTag();
//                if(!layout_set.isShown()){
//                    if(location == 0){
//                        arrow_left.setVisibility(View.GONE);
//                    }else{
//                        arrow_left.setVisibility(View.VISIBLE);
//                    }
//                    if(location == mList.size()-1){
//                        arrow_right.setVisibility(View.GONE);
//                    }else{
//                        arrow_right.setVisibility(View.VISIBLE);
//                    }
//                    layout_bottom.setVisibility(View.VISIBLE);
//                }else{
//                    arrow_left.setVisibility(View.GONE);
//                    arrow_right.setVisibility(View.GONE);
//                    layout_bottom.setVisibility(View.GONE);
//                }
//            }
//        });
//        layout_download.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String url = (String) v.getTag();
//                downloadImage(url);
//            }
//        });
//        layout_set.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                WallpaperManager manager = WallpaperManager.getInstance(context);
//                try {
//                    manager.setBitmap(bitmapUtil.getBitmap((String) v.getTag()));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        arrow_left.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.changePager(LEFT);
//            }
//        });
//        arrow_right.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                listener.changePager(RIGHT);
//            }
//        });
        container.addView(view);
        return view;
    }

//    private void downloadImage(final String _url) {
//        MediaStore.Images.Media.insertImage(context.getContentResolver(),bitmapUtil.getBitmap(_url),MD5.md5(_url),"下载图片");
//        TaskManager.getInstance().executorNewTask(new Runnable() {
//            @Override
//            public void run() {
//                URL url = null;
//                InputStream is = null;
//                try {
//                    url = new URL(_url);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    is = connection.getInputStream();
//                    String fileName = MD5.md5(_url)+".jpg";
//                    File file  = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),fileName);
//                    FileOutputStream fos = new FileOutputStream(file);//对应文件建立输出流
//                    byte[] buffer = new byte[1024];//新建缓存  用来存储 从网络读取数据 再写入文件
//                    int len = 0;
//                    while((len=is.read(buffer)) != -1){//当没有读到最后的时候
//                        fos.write(buffer, 0, len);//将缓存中的存储的文件流秀娥问file文件
//                    }
//                    fos.flush();//将缓存中的写入file
//                    fos.close();
//                    is.close();
//                    ToastUtil.toastLong(context,"下载成功");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//
//    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public interface ViewPagerListener {
        void changePager(int type);
    }
}

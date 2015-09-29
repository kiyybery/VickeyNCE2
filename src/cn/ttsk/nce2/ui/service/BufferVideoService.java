package cn.ttsk.nce2.ui.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import cn.ttsk.library.FileUtil;
import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.entity.DownloadItem;
import cn.ttsk.nce2.entity.Section;
import cn.ttsk.nce2.entity.Video;
import cn.ttsk.nce2.ui.Fragment.DownloadFragment;

public class BufferVideoService extends Service {

	public final static String SID = "sid";
	public final static String VID = "vid";
	public final static String ACTION = "action";
	public final static int ACTION_CONTINUE = 3;
	public final static int ACTION_STOP = 2;
	public final static int ACTION_DELETE = 1;
	public final static int ACTION_START = 0;
	// 执行断点续传的handler
	public static HttpHandler handler1;
	private FinalHttp fh;

	private String vid;

	public List<String> downid = new ArrayList<String>();
	public List<String> downurl = new ArrayList<String>();
	public List<String> downad_card_file = new ArrayList<String>();
	public List<Integer> downposition = new ArrayList<Integer>();

	private int action;
	private String sid;
	private DownloadItem curItem = null;
	private Boolean isDownloading = false;
	private long fullSize;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		fh = new FinalHttp();
		if (intent == null) {
			List<DownloadItem> items = NCE2.db.findAllByWhere(DownloadItem.class, "status=" + DownloadItem.STATUS_DOWNLOADING);
			if (items != null && items.size() > 0) {
				curItem = items.get(0);
				getbuffervideo();
			}
			return START_STICKY;
		}
		sid = intent.getStringExtra(SID);
		vid = intent.getStringExtra(VID);
		Video video = NCE2.db.findById(vid, Video.class);
		if (video == null) {
			return START_STICKY;
		}

		action = intent.getIntExtra(ACTION, ACTION_START);

		switch (action) {
		case ACTION_STOP:// 暂停
			// 如果不在下载列表中则直接忽略
			// 在下载列表中，则修改其状态
			DownloadItem tmpItem = NCE2.db.findById(vid, DownloadItem.class);
			if (tmpItem != null && tmpItem.status == DownloadItem.STATUS_DOWNLOADING) {
				tmpItem.status = DownloadItem.STATUS_PAUSED;
				NCE2.db.update(tmpItem);
				// 如果是当前正在下载的视频，则中断其下载，改为下载下一个
				if (curItem != null && curItem.video_id.equals(vid)) {
					curItem.status = DownloadItem.STATUS_PAUSED;
					handler1.stop();
				}
			}
			
			break;
		case ACTION_DELETE:// 取消
			// 如果不在下载列表中则直接忽略
			// 在下载列表中，则将其删除
			DownloadItem tmpItemD = NCE2.db.findById(vid, DownloadItem.class);
			if (tmpItemD != null && tmpItemD.status == DownloadItem.STATUS_DOWNLOADING) {
				NCE2.db.deleteById(DownloadItem.class, vid);
				// 如果是当前正在下载的视频，则中断其下载，改为下载下一个
				if (curItem != null && curItem.video_id.equals(vid)) {
					handler1.stop();
				}
			}
			
			break;
		case ACTION_CONTINUE:// 继续
			// 如果不在下载列表中则直接忽略
			// 在下载列表中，则修改其状态
			DownloadItem tmpItemC = NCE2.db.findById(vid, DownloadItem.class);
			if (tmpItemC != null && tmpItemC.status != DownloadItem.STATUS_FINISHED) {
				tmpItemC.status = DownloadItem.STATUS_DOWNLOADING;
				NCE2.db.update(tmpItemC);
				if (!isDownloading) {
					curItem = tmpItemC;
					getbuffervideo();
				}
			}
			break;
		default:// 下载
			// 如果在下载列表中则直接忽略，否则加在最后
			DownloadItem tmpItemA = NCE2.db.findById(vid, DownloadItem.class);
			if (tmpItemA == null) {
				DownloadItem newItem = DownloadItem.fromVideo(sid, video,
						DownloadItem.STATUS_DOWNLOADING);
				NCE2.db.save(newItem);
				if (!isDownloading) {
					curItem = newItem;
					getbuffervideo();
				}
			} else if (tmpItemA.status != DownloadItem.STATUS_FINISHED) {
				tmpItemA.status = DownloadItem.STATUS_DOWNLOADING;
				NCE2.db.update(tmpItemA);
				if (!isDownloading) {
					curItem = tmpItemA;
					getbuffervideo();
				}
			}
			break;
		}
		return START_STICKY;
	}

	// 下载数据的方法
	@SuppressWarnings("unchecked")
	public void getbuffervideo() {
		if (isDownloading || curItem == null)
			return;
		isDownloading = true;
		handler1 = fh.download(curItem.url, curItem.tmpFile, true,
				new AjaxCallBack() {

					@Override
					public void onFailure(Throwable t, String strMsg) {
						// TODO
						// Auto-generated
						// method
						// stub
						super.onFailure(t, strMsg);
						isDownloading = false;
						if (action == ACTION_STOP) {
							action = ACTION_START;
						} else if (action == ACTION_DELETE) {
							action = ACTION_START;
							FileUtil.delete(new File(curItem.tmpFile), true);
							FileUtil.delete(new File(curItem.file), true);
						} else {
							DownloadItem tmpItem = NCE2.db.findById(curItem.video_id, DownloadItem.class);
							if (tmpItem != null) {
								tmpItem.status = DownloadItem.STATUS_FAILED;
								NCE2.db.update(tmpItem);
							}
						}
						List<DownloadItem> items = NCE2.db.findAllByWhere(DownloadItem.class, "status=" + DownloadItem.STATUS_DOWNLOADING);
						if (items != null && items.size() > 0) {
							curItem = items.get(0);
							getbuffervideo();
						}
					}

					@Override
					public void onLoading(long count, long current) {
						Intent intent = new Intent();
						intent.putExtra(DownloadFragment.VID, curItem.video_id);
						intent.putExtra(DownloadFragment.DOWNLOADED, current);
						intent.putExtra(DownloadFragment.FULLSIZE, count);
						intent.setAction(NCE2.COURSE_RECEIVER_ACTION);// action与接收器相同
						curItem.size = count;
						curItem.downloaded = current;
						NCE2.db.update(curItem);
						sendBroadcast(intent);
						fullSize = count;
					}

					@Override
					public void onSuccess(Object t) {
						super.onSuccess(t);
						isDownloading = false;
						curItem.status = DownloadItem.STATUS_FINISHED;
						curItem.size = fullSize;
						curItem.downloaded = fullSize;
						NCE2.db.update(curItem);
						new File(curItem.tmpFile).renameTo(new File(
								curItem.file));

						Intent intent = new Intent();
						intent.putExtra(DownloadFragment.VID, curItem.video_id);
						intent.putExtra(DownloadFragment.DOWNLOADED, fullSize);
						intent.putExtra(DownloadFragment.FULLSIZE, fullSize);
						intent.setAction(NCE2.COURSE_RECEIVER_ACTION);// action与接收器相同
						sendBroadcast(intent);

						curItem = null;
						List<DownloadItem> items = NCE2.db.findAllByWhere(DownloadItem.class, "status=" + DownloadItem.STATUS_DOWNLOADING);
						if (items != null && items.size() > 0) {
							curItem = items.get(0);
							getbuffervideo();
						}
					}

				});

	}
}

package cn.ucai.superwechat.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.NetworkImageView;
import com.easemob.EMValueCallBack;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import cn.ucai.superwechat.DemoHXSDKHelper;
import cn.ucai.superwechat.I;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.applib.controller.HXSDKHelper;
import cn.ucai.superwechat.bean.Message;
import cn.ucai.superwechat.bean.User;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.data.MultipartRequest;
import cn.ucai.superwechat.data.RequestManager;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.domain.EMUser;
import cn.ucai.superwechat.listener.OnSetAvatarListener;
import cn.ucai.superwechat.utils.UserUtils;
import cn.ucai.superwechat.utils.Utils;

public class UserProfileActivity extends BaseActivity implements OnClickListener{
	UserProfileActivity mContext;
	private static final String TAG = UserProfileActivity.class.getName();
	private static final int REQUESTCODE_PICK = 1;
	private static final int REQUESTCODE_CUTTING = 2;
	private NetworkImageView headAvatar;
	private ImageView headPhotoUpdate;
	private ImageView iconRightArrow;
	private TextView tvNickName;
	private TextView tvUsername;
	private ProgressDialog dialog;
	private RelativeLayout rlNickName;
	OnSetAvatarListener mOnSetAvatarListener;

	
	
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(cn.ucai.superwechat.R.layout.activity_user_profile);
		initView();
		initListener();
	}
	
	private void initView() {
		headAvatar = (NetworkImageView) findViewById(cn.ucai.superwechat.R.id.user_head_avatar);
		headPhotoUpdate = (ImageView) findViewById(cn.ucai.superwechat.R.id.user_head_headphoto_update);
		tvUsername = (TextView) findViewById(cn.ucai.superwechat.R.id.user_username);
		tvNickName = (TextView) findViewById(cn.ucai.superwechat.R.id.user_nickname);
		rlNickName = (RelativeLayout) findViewById(cn.ucai.superwechat.R.id.rl_nickname);
		iconRightArrow = (ImageView) findViewById(cn.ucai.superwechat.R.id.ic_right_arrow);
	}
	
	private void initListener() {
		Intent intent = getIntent();
		String username = intent.getStringExtra("username");
		String groupId = intent.getStringExtra("groupId");
		boolean enableUpdate = intent.getBooleanExtra("setting", false);
		if (enableUpdate) {
			headPhotoUpdate.setVisibility(View.VISIBLE);
			iconRightArrow.setVisibility(View.VISIBLE);
			rlNickName.setOnClickListener(this);
			headAvatar.setOnClickListener(this);
		} else {
			headPhotoUpdate.setVisibility(View.GONE);
			iconRightArrow.setVisibility(View.INVISIBLE);
		}
		if (username == null) {
			tvUsername.setText(SuperWeChatApplication.getInstance().getUserName());
			UserUtils.setCurrentUserBeanNick(tvNickName);
			UserUtils.setCurrentUserAvatar(headAvatar);
		} else if (username.equals(SuperWeChatApplication.getInstance().getUserName())) {
			tvUsername.setText(SuperWeChatApplication.getInstance().getUserName());
			UserUtils.setCurrentUserNick(tvNickName);
			UserUtils.setCurrentUserAvatar(headAvatar);
		} else {
			if (groupId!=null) {
				UserUtils.setUserAvatar(UserUtils.getAvatarPath(username), headAvatar);
				UserUtils.setGroupMemberNick(groupId,username,tvNickName);
			} else {
				UserUtils.setUserBeanNick(username, tvNickName);
				UserUtils.setUserBeanAvatar(username, headAvatar);
			}

			tvUsername.setText(username);

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case cn.ucai.superwechat.R.id.user_head_avatar:
			mOnSetAvatarListener = new OnSetAvatarListener(mContext, R.id.layout_avatar, getAvatarName(),I.AVATAR_TYPE_USER_PATH);
//			uploadHeadPhoto();
			break;

		case cn.ucai.superwechat.R.id.rl_nickname:
			final EditText editText = new EditText(this);
			new AlertDialog.Builder(this).setTitle(cn.ucai.superwechat.R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
					.setPositiveButton(cn.ucai.superwechat.R.string.dl_ok, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String nickString = editText.getText().toString();
							if (TextUtils.isEmpty(nickString)) {
								Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
								return;
							}
							updateUserNick(nickString);
						}
					}).setNegativeButton(cn.ucai.superwechat.R.string.dl_cancel, null).show();
			break;
		default:
			break;
		}

	}
	String AvatarName;

	private String getAvatarName() {
		AvatarName = System.currentTimeMillis()+"";
		return AvatarName;


	}

	public void asyncFetchUserInfo(String username){
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).getUserProfileManager().asyncGetUserInfo(username, new EMValueCallBack<EMUser>() {
			
			@Override
			public void onSuccess(EMUser user) {
				if (user != null) {
					tvNickName.setText(user.getNick());
					if(!TextUtils.isEmpty(user.getAvatar())){
						 Picasso.with(UserProfileActivity.this).load(user.getAvatar()).placeholder(cn.ucai.superwechat.R.drawable.default_avatar).into(headAvatar);
					}else{
						Picasso.with(UserProfileActivity.this).load(cn.ucai.superwechat.R.drawable.default_avatar).into(headAvatar);
					}
					UserUtils.saveUserInfo(user);
				}
			}
			
			@Override
			public void onError(int error, String errorMsg) {
			}
		});
	}
	
	
	
	private void uploadHeadPhoto() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(cn.ucai.superwechat.R.string.dl_title_upload_photo);
		builder.setItems(new String[] { getString(cn.ucai.superwechat.R.string.dl_msg_take_photo), getString(cn.ucai.superwechat.R.string.dl_msg_local_upload) },
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_no_support),
									Toast.LENGTH_SHORT).show();
							break;
						case 1:
							Intent pickIntent = new Intent(Intent.ACTION_PICK,null);
							pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
							startActivityForResult(pickIntent, REQUESTCODE_PICK);
							break;
						default:
							break;
						}
					}
				});
		builder.create().show();
	}

	private void updateUserNick(String nickName) {
		try {
			String path = new ApiParams()
                    .with(I.User.USER_NAME, SuperWeChatApplication.getInstance().getUserName())
                    .with(I.User.NICK, nickName)
                    .getRequestUrl(I.REQUEST_UPDATE_USER_NICK);
			executeRequest(new GsonRequest<User>(path,User.class,
					responseUpdateNickListener(nickName),errorListener()));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Response.Listener<User> responseUpdateNickListener(final String nickName) {
		return new Response.Listener<User>() {
			@Override
			public void onResponse(User user) {
				if (user != null && user.isResult()) {
					updateRemoteNick(nickName);

				} else {
					Utils.showToast(mContext,user.getMsg(),Toast.LENGTH_SHORT);
				}
			}
		};

	}


	private void updateRemoteNick(final String nickName) {
		dialog = ProgressDialog.show(this, getString(cn.ucai.superwechat.R.string.dl_update_nick),
				getString(cn.ucai.superwechat.R.string.dl_waiting));
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean updatenick = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().updateParseNickName(nickName);
				if (UserProfileActivity.this.isFinishing()) {
					return;
				}
				if (!updatenick) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(UserProfileActivity.this,
									getString(cn.ucai.superwechat.R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
									.show();
							dialog.dismiss();
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
							Toast.makeText(UserProfileActivity.this,
									getString(cn.ucai.superwechat.R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
									.show();
							tvNickName.setText(nickName);
							/**
                             * 全局变量中Nick的更新，
							 * 以及UserDao中的Nick的更新
							 */
							SuperWeChatApplication.currentUserNick = nickName;
							User user = SuperWeChatApplication.getInstance().getUser();
							user.setMUserNick(nickName);
							UserDao userDao = new UserDao(UserProfileActivity.this);
							userDao.updateUser(user);

						}
					});
				}
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch (requestCode) {
//		case REQUESTCODE_PICK:
//			if (data == null || data.getData() == null) {
//				return;
//			}
//			startPhotoZoom(data.getData());
//			break;
//		case REQUESTCODE_CUTTING:
//			if (data != null) {
//				setPicToView(data);
//			}
//			break;
//		default:
//			break;
//		}
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode!=RESULT_OK) {
			return;
		}
		mOnSetAvatarListener.setAvatar(requestCode,data,headAvatar);
		if (requestCode==OnSetAvatarListener.REQUEST_CROP_PHOTO) {
			updateUserAvatar();
		}

	}

	private final String boundary = "apiclient" + System.currentTimeMillis();

	private final String mimeType = "multipart/from-data;boundary=" + boundary;

	byte[] multipartBody;

	private void updateUserAvatar() {
		dialog = ProgressDialog.show(this, getString(cn.ucai.superwechat.R.string.dl_update_photo),
				getString(cn.ucai.superwechat.R.string.dl_waiting));
		dialog.show();
		try {
			String url = new ApiParams()
                    .with(I.AVATAR_TYPE, I.AVATAR_TYPE_USER_PATH)
                    .with(I.User.USER_NAME, SuperWeChatApplication.getInstance().getUserName())
                    .getRequestUrl(I.REQUEST_UPLOAD_AVATAR);
			executeRequest(new MultipartRequest<Message>(url,Message.class,null,
					requestUpdateUserAvatarListerner(),errorListener(),mimeType,multipartBody));

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	private Response.Listener<Message> requestUpdateUserAvatarListerner() {

		return new Response.Listener<Message>() {
			@Override
			public void onResponse(Message message) {
				if (message.isResult()) {
					RequestManager.getRequestQueue()
							.getCache().remove(UserUtils.getAvatarPath(SuperWeChatApplication
							.getInstance().getUserName()));
					UserUtils.setCurrentUserAvatar(headAvatar);
				} else {
					UserUtils.setCurrentUserAvatar(headAvatar);
					Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_updatephoto_fail),
							Toast.LENGTH_SHORT).show();


				}
				dialog.dismiss();


			}
		};
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}
	
	/**
	 * save the picture data
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(getResources(), photo);
			headAvatar.setImageDrawable(drawable);
			uploadUserAvatar(Bitmap2Bytes(photo));
		}

	}
	
	private void uploadUserAvatar(final byte[] data) {
		dialog = ProgressDialog.show(this, getString(cn.ucai.superwechat.R.string.dl_update_photo), getString(cn.ucai.superwechat.R.string.dl_waiting));
		new Thread(new Runnable() {

			@Override
			public void run() {
				final String avatarUrl = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().uploadUserAvatar(data);
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
						if (avatarUrl != null) {
							Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_updatephoto_success),
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(UserProfileActivity.this, getString(cn.ucai.superwechat.R.string.toast_updatephoto_fail),
									Toast.LENGTH_SHORT).show();
						}

					}
				});

			}
		}).start();

		dialog.show();
	}
	
	
	public byte[] Bitmap2Bytes(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
}

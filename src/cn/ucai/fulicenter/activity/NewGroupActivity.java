/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.ucai.fulicenter.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

import java.io.File;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.bean.Contact;
import cn.ucai.fulicenter.bean.Group;
import cn.ucai.fulicenter.bean.Message;
import cn.ucai.fulicenter.bean.User;
import cn.ucai.fulicenter.data.ApiParams;
import cn.ucai.fulicenter.data.GsonRequest;
import cn.ucai.fulicenter.data.OkHttpUtils;
import cn.ucai.fulicenter.listener.OnSetAvatarListener;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.utils.Utils;

public class NewGroupActivity extends BaseActivity {
	private EditText groupNameEditText;
	private ProgressDialog progressDialog;
	private EditText introductionEditText;
	private CheckBox checkBox;
	private CheckBox memberCheckbox;
	private ImageView groupsAvatar;
	private LinearLayout openInviteContainer;
	private final static String TAG = NewGroupActivity.class + "";

	NewGroupActivity mContext;
	private static final int CREATE_NEW_GROUP = 200;
	OnSetAvatarListener mOnSetAvatarListener;
	String avatarName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(cn.ucai.fulicenter.R.layout.activity_new_group);
		initView();
		setListener();
	}
	private void initView() {
		groupNameEditText = (EditText) findViewById(cn.ucai.fulicenter.R.id.edit_group_name);
		introductionEditText = (EditText) findViewById(cn.ucai.fulicenter.R.id.edit_group_introduction);
		checkBox = (CheckBox) findViewById(cn.ucai.fulicenter.R.id.cb_public);
		memberCheckbox = (CheckBox) findViewById(cn.ucai.fulicenter.R.id.cb_member_inviter);
		openInviteContainer = (LinearLayout) findViewById(cn.ucai.fulicenter.R.id.ll_open_invite);
		groupsAvatar = (ImageView) findViewById(R.id.cb_Avatar);


	}

	/**
	 * 设置监听器
	 */
	private void setListener() {
		setOnCheckChangedListener();
		setSaveGroupClickListener();
		setGroupIconClickListener();
	}



	private void setGroupIconClickListener() {
		findViewById(R.id.cb_Avatar).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mOnSetAvatarListener = new OnSetAvatarListener(NewGroupActivity.this, R.id.layout_new_group,
						getAvatarName(), I.AVATAR_TYPE_GROUP_PATH);



			}
		});


	}

	private void setSaveGroupClickListener() {
		findViewById(R.id.btnSaveGroup).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String str6 = getResources().getString(cn.ucai.fulicenter.R.string.Group_name_cannot_be_empty);
				String name = groupNameEditText.getText().toString();
				if (TextUtils.isEmpty(name)) {
					Intent intent = new Intent(mContext, AlertDialog.class);
					intent.putExtra("msg", str6);
					startActivity(intent);
				} else {
					// 进通讯录选人
					startActivityForResult(new Intent(NewGroupActivity.this, GroupPickContactsActivity.class).putExtra("groupName", name), CREATE_NEW_GROUP);
				}

			}
		});


	}

	private void setOnCheckChangedListener() {
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					openInviteContainer.setVisibility(View.INVISIBLE);
				}else{
					openInviteContainer.setVisibility(View.VISIBLE);
				}
			}
		});


	}



	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode!=RESULT_OK) {
			return;
		}
		if (requestCode == CREATE_NEW_GROUP) {
			//新建群组
			setProgressDialog();
			createNewGroup(data);
		} else {
			mOnSetAvatarListener.setAvatar(requestCode,data,groupsAvatar);

		}
	}

	private void createNewGroup(final Intent data) {
			final String st2 = getResources().getString(cn.ucai.fulicenter.R.string.Failed_to_create_groups);
		new Thread(new Runnable() {
            @Override
            public void run() {
                // 调用sdk创建群组方法
                String groupName = groupNameEditText.getText().toString().trim();
                String desc = introductionEditText.getText().toString();
				Contact[] contacts = (Contact[]) data.getSerializableExtra("newmembers");
				String[] members = null;
				String[] membersID = null;
				if (contacts != null) {
					members = new String[contacts.length];
					for (int i=0;i<contacts.length;i++) {
						members[i] = contacts[i].getMContactCname()+",";
						membersID[i] = contacts[i].getMContactId()+",";
					}
				}
				EMGroup emGroup;
                try {
                    if(checkBox.isChecked()){
                        //创建公开群，此种方式创建的群，可以自由加入
                        //创建公开群，此种方式创建的群，用户需要申请，等群主同意后才能加入此群
                       emGroup= EMGroupManager.getInstance().createPublicGroup(groupName, desc, members, true,200);
                    }else{
                        //创建不公开群
						emGroup = EMGroupManager.getInstance().createPrivateGroup(groupName, desc, members, memberCheckbox.isChecked(), 200);
					}
					String hxid = emGroup.getGroupId();
					createNewGroupAppServer(emGroup.getGroupId(),groupName,desc,contacts);

					runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            setResult(RESULT_OK);
                            finish();
                        }
                    });
                } catch (final EaseMobException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(NewGroupActivity.this, st2 + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }
        }).start();
	}

	private void createNewGroupAppServer(String hxid, String gourpName,
										 String desc, final Contact[]contacts) {
		User user = SuperWeChatApplication.getInstance().getUser();
		boolean isPublic = checkBox.isChecked();
		boolean isInvite = memberCheckbox.isChecked();
		/**
		 * 注册环信服务器
		 * 注册远端服务器并上传头像
		 * 添加群成员
		 */
		File file = new File(ImageUtils.getAvatarPath(mContext,I.AVATAR_TYPE_GROUP_PATH),
				avatarName+I.AVATAR_SUFFIX_JPG);
		OkHttpUtils<Group> utils = new OkHttpUtils<>();
		utils.url(SuperWeChatApplication.SERVER_ROOT)
				.addParam(I.KEY_REQUEST, I.REQUEST_CREATE_GROUP)
				.addParam(I.Group.GROUP_ID, hxid)
				.addParam(I.Group.NAME, gourpName)
				.addParam(I.Group.DESCRIPTION, desc)
				.addParam(I.Group.OWNER, user.getMUserName())
				.addParam(I.Group.IS_PUBLIC, isPublic + "")
				.addParam(I.Group.ALLOW_INVITES, isInvite + "")
				.addFile(file)
				.targetClass(Group.class)
				.execute(new OkHttpUtils.OnCompleteListener<Group>() {
					@Override
					public void onSuccess(Group result) {
						if (result.isResult()) {
							if (contacts != null) {
								addGroupMembers(contacts,result);

							} else {
								SuperWeChatApplication.getInstance().getGroupList().add(result);
								progressDialog.dismiss();
								setResult(RESULT_OK);
								mContext.sendBroadcast(new Intent("update_group_list"));
								finish();

							}

						} else {
							Utils.showToast(mContext,Utils.getResourceString(mContext,result.getMsg()),Toast.LENGTH_SHORT);
							progressDialog.dismiss();


						}
					}

					@Override
					public void onError(String error) {
						progressDialog.dismiss();
						Utils.showToast(mContext, error, Toast.LENGTH_LONG);
						Log.i(TAG, "register fail error : " + error);
					}
				});

	}

	private void addGroupMembers(Contact[] contacts,Group group) {
		String userIds = "";
		String userName = "";
		Integer mGroupId = group.getMGroupId();
		for (int i=0;i<contacts.length;i++) {
			userIds += contacts[i].getMContactCid() + ",";
			userName += contacts[i].getMContactCname() + ",";

		}

		try {
			String path = new ApiParams()
                    .with(I.Member.USER_ID, userIds)
                    .with(I.Member.USER_NAME, userName)
                    .with(I.Member.GROUP_HX_ID, mGroupId + "")
                    .getRequestUrl(I.REQUEST_ADD_GROUP_MEMBERS);
			executeRequest(new GsonRequest<Message>(path,Message.class,
					responseAddMemebersListener(group),errorListener()));
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	private Response.Listener<Message> responseAddMemebersListener(final Group group) {
		return new Response.Listener<Message>() {
			@Override
			public void onResponse(Message message) {
				if (message.isResult()) {
					progressDialog.dismiss();
					SuperWeChatApplication.getInstance().getGroupList().add(group);
					Intent intent = new Intent("update_group_list").putExtra("group", group);
					Utils.showToast(mContext, group.getMsg(), Toast.LENGTH_SHORT);
					setResult(RESULT_OK, intent);


				} else {
					progressDialog.dismiss();
					Utils.showToast(mContext, R.string.Failed_to_create_groups, Toast.LENGTH_SHORT);
				}
				finish();
			}
		};
	}


	private void setProgressDialog() {
		String st1 = getResources().getString(R.string.Is_to_create_a_group_chat);
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(st1);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	public void back(View view) {
		finish();
	}


	public String getAvatarName() {
		avatarName = System.currentTimeMillis()+"";
		return avatarName;
	}
}

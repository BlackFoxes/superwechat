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

package cn.ucai.superwechat.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.Group;
import cn.ucai.superwechat.utils.UserUtils;

public class GroupSimpleDetailActivity extends BaseActivity {
	private Button btn_add_group;
	private TextView tv_admin;
	private TextView tv_name;
	private TextView tv_introduction;
	private Group group;
	private String groupid;
	private ProgressBar progressBar;
	private NetworkImageView mv_avatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(cn.ucai.superwechat.R.layout.activity_group_simle_details);
		tv_name = (TextView) findViewById(cn.ucai.superwechat.R.id.name);
		tv_admin = (TextView) findViewById(cn.ucai.superwechat.R.id.tv_admin);
		btn_add_group = (Button) findViewById(cn.ucai.superwechat.R.id.btn_add_to_group);
		tv_introduction = (TextView) findViewById(cn.ucai.superwechat.R.id.tv_introduction);
		progressBar = (ProgressBar) findViewById(cn.ucai.superwechat.R.id.loading);
		mv_avatar = (NetworkImageView) findViewById(R.id.avatar);

		Group groupInfo = (Group) getIntent().getSerializableExtra("groupinfo");
		String groupname = null;
		if(groupInfo != null){
			group = groupInfo;
			groupname = groupInfo.getMGroupName();
		    groupid = groupInfo.getMGroupHxid();
		}else{
//		    group = PublicGroupsSeachActivity.searchedGroup;
//		    if(group == null)
//		        return;
//		    groupname = group.getGroupName();
//		    groupid = group.getGroupId();
		}
		
		tv_name.setText(groupname);
		
		
		if(group != null){
		    showGroupDetail();
		    return;
		}
//		new Thread(new Runnable() {
//
//			public void run() {
//				//从服务器获取详情
//				try {
//					group = EMGroupManager.getInstance().getGroupFromServer(groupid);
//					runOnUiThread(new Runnable() {
//						public void run() {
//							showGroupDetail();
//						}
//					});
//				} catch (final EaseMobException e) {
//					e.printStackTrace();
//					final String st1 = getResources().getString(cn.ucai.superwechat.R.string.Failed_to_get_group_chat_information);
//					runOnUiThread(new Runnable() {
//						public void run() {
//							progressBar.setVisibility(View.INVISIBLE);
//							Toast.makeText(GroupSimpleDetailActivity.this, st1+e.getMessage(), 1).show();
//						}
//					});
//				}
//
//			}
//		}).start();
		
	}
	
//	//加入群聊
//	public void addToGroup(View view){
//		String st1 = getResources().getString(cn.ucai.superwechat.R.string.Is_sending_a_request);
//		final String st2 = getResources().getString(cn.ucai.superwechat.R.string.Request_to_join);
//		final String st3 = getResources().getString(cn.ucai.superwechat.R.string.send_the_request_is);
//		final String st4 = getResources().getString(cn.ucai.superwechat.R.string.Join_the_group_chat);
//		final String st5 = getResources().getString(cn.ucai.superwechat.R.string.Failed_to_join_the_group_chat);
//		final ProgressDialog pd = new ProgressDialog(this);
////		getResources().getString(R.string)
//		pd.setMessage(st1);
//		pd.setCanceledOnTouchOutside(false);
//		pd.show();
//		new Thread(new Runnable() {
//			public void run() {
//				try {
//					//如果是membersOnly的群，需要申请加入，不能直接join
//					if(group.isMembersOnly()){
//					    EMGroupManager.getInstance().applyJoinToGroup(groupid, st2);
//					}else{
//					    EMGroupManager.getInstance().joinGroup(groupid);
//					}
//					runOnUiThread(new Runnable() {
//						public void run() {
//							pd.dismiss();
//							if(group.isMembersOnly())
//								Toast.makeText(GroupSimpleDetailActivity.this, st3, Toast.LENGTH_SHORT).show();
//							else
//								Toast.makeText(GroupSimpleDetailActivity.this, st4, Toast.LENGTH_SHORT).show();
//							btn_add_group.setEnabled(false);
//						}
//					});
//				} catch (final EaseMobException e) {
//					e.printStackTrace();
//					runOnUiThread(new Runnable() {
//						public void run() {
//							pd.dismiss();
//							Toast.makeText(GroupSimpleDetailActivity.this, st5+e.getMessage(), Toast.LENGTH_SHORT).show();
//						}
//					});
//				}
//			}
//		}).start();
//	}
	
     private void showGroupDetail() {
         progressBar.setVisibility(View.INVISIBLE);
         //获取详情成功，并且自己不在群中，才让加入群聊按钮可点击
         if(!SuperWeChatApplication.getInstance().getPublicGroupList().contains(group))
             btn_add_group.setEnabled(true);
         tv_name.setText(group.getMGroupName());
         tv_admin.setText(group.getMGroupOwner());
         tv_introduction.setText(group.getMGroupDescription());
		 UserUtils.setGroupBeanAvatar(groupid,mv_avatar);
     }
	
	public void back(View view){
		finish();
	}
}

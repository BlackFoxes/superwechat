package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Response;

import java.util.ArrayList;
import java.util.HashMap;

import cn.ucai.superwechat.I;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.activity.BaseActivity;
import cn.ucai.superwechat.bean.Member;
import cn.ucai.superwechat.data.ApiParams;
import cn.ucai.superwechat.data.GsonRequest;
import cn.ucai.superwechat.data.OkHttpUtils;

/**
 * Created by sks on 2016/5/31.
 */
public class DownloadGroupMemberTask extends BaseActivity{
    public static final String TAG = DownloadContactListTask.class.getName();
    Context mContext;
    String hxid;
    String path;

    public DownloadGroupMemberTask(Context mContext, String hxid) {
        this.mContext = mContext;
        this.hxid = hxid;
        initPath();
    }

    /*http://10.0.2.2:8080/SuperWeChatServer/Server?
    request=download_contact_all_list&m_contact_user_name=*/
    public void initPath() {
        try {
            path = new ApiParams().with(I.Member.GROUP_HX_ID, hxid)
                    .getRequestUrl(I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execute() {
        executeRequest(new GsonRequest<Member[]>(path, Member[].class,
                responseDownloadGroupMemberListListener(),errorListener()));
    }

    private Response.Listener<Member[]> responseDownloadGroupMemberListListener() {
        return new Response.Listener<Member[]>() {
            @Override
            public void onResponse(Member[] members) {
                Log.e(TAG,"DownloadGroupMember");
                if (members != null) {
                    Log.e(TAG,"DownloadGroupMember,members size="+members.length);
                    return;
                }
                ArrayList<Member> list = OkHttpUtils.array2List(members);
                HashMap<String, ArrayList<Member>> groupMembers = SuperWeChatApplication.getInstance().getGroupMembers();
                ArrayList<Member> members1 = groupMembers.get(hxid);
                members1.clear();
                members1.addAll(list);
                mContext.sendStickyBroadcast(new Intent("update_member_List"));
            }
        };
    }
}

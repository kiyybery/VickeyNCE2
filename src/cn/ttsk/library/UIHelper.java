/**
 * 
 */
package cn.ttsk.library;

import cn.ttsk.nce2.NCE2;
import cn.ttsk.nce2.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class UIHelper {
    
    public static Dialog buildConfirm(Context context, String title, String left,String right, android.view.View.OnClickListener listener_left, android.view.View.OnClickListener listener_right) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        View view = View.inflate(context, R.layout.dialog_nce2_confirm, null);
        RelayoutViewTool.relayoutViewWithScale(view, NCE2.screenWidthScale);
        
        TextView tv_title = (TextView) view.findViewById(R.id.tv_dialog_confirm_title);
        tv_title.setText(title);
        Button btn_left = (Button) view.findViewById(R.id.btn_dialog_confirm_left);
        btn_left.setText(left);
        btn_left.setOnClickListener(listener_left);
        Button btn_right = (Button) view.findViewById(R.id.btn_dialog_confirm_right);
        btn_right.setText(right);
        btn_right.setOnClickListener(listener_right);        
        window.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }
    
    public static Dialog buildAlert(Context context, String title, String content, String str_button, android.view.View.OnClickListener listener) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        dialog.show();
        Window window = dialog.getWindow();
        View view = View.inflate(context, R.layout.dialog_nce2_alert, null);
        RelayoutViewTool.relayoutViewWithScale(view, NCE2.screenWidthScale);
        
        TextView tv_content = (TextView) view.findViewById(R.id.tv_dialog_alert_content);
        tv_content.setText(Html.fromHtml(content));
        Button btn_confirm = (Button) view.findViewById(R.id.btn_dialog_alert);
        btn_confirm.setText(str_button);
        btn_confirm.setOnClickListener(listener);       
        window.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
	}
}

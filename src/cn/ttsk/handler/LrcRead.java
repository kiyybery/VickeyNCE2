package cn.ttsk.handler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;

import cn.ttsk.nce2.entity.LyricContent;



public class LrcRead {

	private List<LyricContent> LyricList;

	private LyricContent mLyricContent;


	public LrcRead(){

		mLyricContent=new LyricContent();

		LyricList=new ArrayList<LyricContent>();

	}

	public void Read(File file) throws FileNotFoundException,IOException{

		String Lrc_data="";
		FileInputStream mFileInputStream = new FileInputStream(file);
		InputStreamReader mInputStreamReader= new InputStreamReader(mFileInputStream,"GB2312");

		//
		BufferedReader mBufferedReader=new BufferedReader(mInputStreamReader);

		while((Lrc_data=mBufferedReader.readLine())!=null){

			Lrc_data=Lrc_data.replace("[", "");

			Lrc_data=Lrc_data.replace("]", "@");

			String splitLrc_data[]=Lrc_data.split("@");

			if(splitLrc_data.length>1){
				
				mLyricContent.setLyric(splitLrc_data[1]);
				
				int LyricTime= TimeStr(splitLrc_data[0]);
				
				mLyricContent.setLyricTime(LyricTime);
				
				LyricList.add(mLyricContent);
				
				mLyricContent=new LyricContent();
			}

		}

		mBufferedReader.close();

		mInputStreamReader.close();
	}
        
	    public int TimeStr(String timeStr){
			
			String timeData1[]=timeStr.split(":");
			
			int minute = Integer.parseInt(timeData1[0]);
			int second = 0;
			int millisecond = 0;
			if (timeData1.length == 2) {
				if (timeData1[1].indexOf(".")> 0) {
					String timeData2[] = timeData1[1].split(".");
					second=Integer.parseInt(timeData2[0]);
					millisecond = Integer.parseInt(timeData2[1]);
				} else {
					second = Integer.parseInt(timeData1[1]);
				}
			}
			
			
			int currentTime=(minute*60+second)*1000+millisecond*10;
	    	
			return currentTime;
	    }
      
	    public List<LyricContent> GetLyricContent(){
	    	
			return LyricList;
	    }
}


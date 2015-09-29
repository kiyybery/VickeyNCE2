package cn.ttsk.library;

public class LameUtil {
	static {
		System.loadLibrary("mp3lame");
	}

	private native void initEncoder(int numChannels, int sampleRate,
			int bitRate, int mode, int quality);

	private native void destroyEncoder();
	
	private native int encodeFile(String sourcePath, String targetPath);
	
	public void init(int numChannels, int sampleRate,
			int bitRate, int mode, int quality) {
		initEncoder(numChannels, sampleRate,
				bitRate, mode, quality);
	}
	
	public void destroy() {
		destroyEncoder();
	}
	
	public int encode(String sourcePath, String targetPath) {
		return encodeFile(sourcePath, targetPath);
	}
}

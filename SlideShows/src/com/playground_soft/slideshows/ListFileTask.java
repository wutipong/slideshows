package com.playground_soft.slideshows;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;
import android.os.AsyncTask;
import android.util.Log;

class ListFileTask extends AsyncTask<SmbFile, Void, SmbFile[]> {

	/**
	 * 
	 */
	private final TaskDone taskDone;

	/**
	 * @param imageViewerFragment
	 */
	ListFileTask(TaskDone taskDone) {
		this.taskDone = taskDone;
	}

	@Override
	protected SmbFile[] doInBackground(SmbFile... params) {
		try {
			/*String path = params[0];
			String domain = params[1];
			String userid = params[2];
			String password = params[3];
			
			NtlmPasswordAuthentication auth = 
					new NtlmPasswordAuthentication(domain, userid, password);
			file = new SmbFile(path, auth);
			*/
			
			SmbFile parent = params[0];

			SmbFileFilter filterImg = new SmbFileFilter() {

				@Override
				public boolean accept(SmbFile arg0) throws SmbException {
					String filename = arg0.getName().toLowerCase();
					if (filename.endsWith(".jpg")
							|| filename.endsWith(".jpeg")
							|| filename.endsWith(".png")) {
						return true;
					}
					return false;
				}
			};

			SmbFile[] imgFiles = parent.listFiles(filterImg);
			Comparator<SmbFile> comparator = new Comparator<SmbFile>() {

				@Override
				public int compare(SmbFile lhs, SmbFile rhs) {
					return lhs.getName().compareToIgnoreCase(rhs.getName());
				}

			};

			Arrays.sort(imgFiles, comparator);

			return imgFiles;

		} catch (IOException e) {
			Log.e("cifs", e.getMessage(), e);
		}
		return null;
	}

	@Override
	protected void onPostExecute(SmbFile[] result) {
		super.onPostExecute(result);
		taskDone.onTaskDone(result);
		
	}

	public abstract static class TaskDone {
		public abstract void onTaskDone(SmbFile[] result);
	}
}
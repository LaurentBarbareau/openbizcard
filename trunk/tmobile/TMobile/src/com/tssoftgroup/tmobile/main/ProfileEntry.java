package com.tssoftgroup.tmobile.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Vector;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreNotFoundException;

public class ProfileEntry {
	private static ProfileEntry instance;

	private final String PROFILE_NAME = "TMOBILEMCAST";

	public String name = "";
	public String email = "";
	public String mobile = "";
	public String passCode = "";
	// videoname,status,scheduleTime (0 or time) |
	// Status  0= new| 1=will be downloaded | 2=downloading | 3=downloaded
	// 1754514.mp4,0, 40:11 5/6/2010 | 1754515.mp4,0,12:4 5/6/2010
	public String videos = ""; 
	public String roaming = "Off"; // On , Off
	public String settingTime = ""; // long
	Vector bookmarks;

	public String toString() {
		StringBuffer ret = new StringBuffer();
		ret.append("needLogin");
		ret.append(name);
		ret.append('\n');

		ret.append("apiKey");
		ret.append(email);
		ret.append('\n');

		ret.append("secretCode");
		ret.append(mobile);
		ret.append('\n');

		ret.append("miniKey");
		ret.append(passCode);
		ret.append('\n');

		ret.append("videos");
		ret.append(videos);
		ret.append('\n');

		ret.append("roaming");
		ret.append(roaming);
		ret.append('\n');
		
		ret.append("settingTime");
		ret.append(settingTime);
		ret.append('\n');
		
		return ret.toString();
	}

	private ProfileEntry() {
		bookmarks = new Vector();
	}

	public static ProfileEntry getInstance() {
		if (instance == null) {
			instance = new ProfileEntry();
		}
		return instance;
	}

	public void clearAll() {
		name = "";
		email = "";
		mobile = "";
		passCode = "";
		videos = "";
		roaming = "Off";
		settingTime = "";
	}

	public ProfileEntry(byte[] buf) {
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);
		DataInputStream dis = new DataInputStream(bais);
		try {
			name = dis.readUTF();
			email = dis.readUTF();
			mobile = dis.readUTF();
			passCode = dis.readUTF();
			videos = dis.readUTF();
			roaming = dis.readUTF();
			settingTime = dis.readUTF();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static ProfileEntry toProfileEntry(byte[] buf) {
		ProfileEntry profileEntry = new ProfileEntry(buf);
		return profileEntry;

	}

	public byte[] toBytes() {
		byte data[] = null;

		try {
			checkNull();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeUTF(name);
			dos.writeUTF(email);
			dos.writeUTF(mobile);
			dos.writeUTF(passCode);
			dos.writeUTF(videos);
			dos.writeUTF(roaming);
			dos.writeUTF(settingTime);
			data = baos.toByteArray();
			baos.close();
			dos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

	// public void SaveProfile(String aPhoneNO, String aName, String aNickName,
	// String aEmail, String aAddress, String aCompany, String aHomeTel, String
	// aOther, long aBirthdate)
	public void saveProfile() {
		RecordStore rs = null;
		try {
			RecordStore.deleteRecordStore(PROFILE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			rs = RecordStore.openRecordStore(PROFILE_NAME, true);
			// for (int i = bookmarks.size() - 1; i >= 0; i--)
			// {
			// ProfileEntry be = (ProfileEntry) bookmarks.elementAt(0);
			rs.addRecord(toBytes(), 0, toBytes().length);
			// }
			// System.out.println("finish save");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.closeRecordStore();
				rs = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void loadProfile() throws Exception {

		bookmarks = new Vector();
		if (recordStoreExists(PROFILE_NAME) == true) {
			RecordStore rs = null;
			RecordEnumeration re = null;
			try {
				rs = RecordStore.openRecordStore(PROFILE_NAME, false);
				re = rs.enumerateRecords(null, null, false);
				re.reset();
				while (re.hasNextElement()) {
					try {
						bookmarks.addElement(new ProfileEntry(re.nextRecord()));
					} catch (Exception e) {
					}
				}
				ProfileEntry be = (ProfileEntry) bookmarks.elementAt(0);

				name = be.name;
				email = be.email;
				mobile = be.mobile;
				passCode = be.passCode;
				videos = be.videos;
				roaming = be.roaming;
				settingTime = be.settingTime;
				checkNull();

				// System.out.println(this);
			} catch (Exception e) {
				e.printStackTrace();
//				checkNull();
			} finally {
				try {
					re.destroy();
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					rs.closeRecordStore();

				} catch (Exception e) {
					e.printStackTrace();
				}
				re = null;
				rs = null;
				System.gc();

			}
		}
	}

	public boolean recordStoreExists(String RecordStoreName) {
		boolean existF = true;
		RecordStore rs = null;
		try {
			rs = RecordStore.openRecordStore(RecordStoreName, false);
		} catch (RecordStoreNotFoundException e) {
			existF = false;
		} catch (Exception e) {

		} finally {
			try {
				rs.closeRecordStore();
				rs = null;
				System.gc();
			} catch (Exception e) {

			}
		}
		// System.out.println("Have record store = " + existF);
		return existF;
	}

	private void checkNull() {
		if (name == null) {
			name = "";
		}

		if (email == null) {
			email = "";
		}
		if (mobile == null) {
			mobile = "";
		}
		if (passCode == null) {
			passCode = "";
		}
		if (videos == null) {
			videos = "";
		}
		if (roaming == null) {
			roaming = "Off";
		}
		if (settingTime == null) {
			settingTime = "";
		}
	}
}

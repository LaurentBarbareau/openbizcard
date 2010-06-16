package com.tssoftgroup.tmobile.component.engine;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Status;

import com.tssoftgroup.tmobile.component.BitmapFieldWithStatus;
import com.tssoftgroup.tmobile.component.engine.json.JSONArray;
import com.tssoftgroup.tmobile.component.engine.json.JSONException;
import com.tssoftgroup.tmobile.component.engine.json.JSONObject;
import com.tssoftgroup.tmobile.main.ProfileEntry;
import com.tssoftgroup.tmobile.model.Choice;
import com.tssoftgroup.tmobile.model.Comment;
import com.tssoftgroup.tmobile.model.DocumentInfo;
import com.tssoftgroup.tmobile.model.MoreInfo;
import com.tssoftgroup.tmobile.model.PicInfo;
import com.tssoftgroup.tmobile.model.PollInfo;
import com.tssoftgroup.tmobile.model.ProjectInfo;
import com.tssoftgroup.tmobile.model.Question;
import com.tssoftgroup.tmobile.model.RawTrainingAns;
import com.tssoftgroup.tmobile.model.TrainingInfo;
import com.tssoftgroup.tmobile.model.User;
import com.tssoftgroup.tmobile.model.Video;
import com.tssoftgroup.tmobile.screen.DocumentListScreen;
import com.tssoftgroup.tmobile.screen.MCastDetail;
import com.tssoftgroup.tmobile.screen.MCastPlayerScreen;
import com.tssoftgroup.tmobile.screen.MCastScreen;
import com.tssoftgroup.tmobile.screen.PollListScreen;
import com.tssoftgroup.tmobile.screen.PollQuestionScreen;
import com.tssoftgroup.tmobile.screen.ProjectListScreen;
import com.tssoftgroup.tmobile.screen.TrainingListScreen;
import com.tssoftgroup.tmobile.screen.TrainingQuestionScreen;
import com.tssoftgroup.tmobile.screen.VideoConnectDetail;
import com.tssoftgroup.tmobile.screen.VideoConnectPlayerScreen;
import com.tssoftgroup.tmobile.screen.VideoConnectScreen;
import com.tssoftgroup.tmobile.screen.WaitScreen;
import com.tssoftgroup.tmobile.utils.Const;
import com.tssoftgroup.tmobile.utils.CrieUtils;
import com.tssoftgroup.tmobile.utils.DownloadCombiner;
import com.tssoftgroup.tmobile.utils.StringUtil;
import com.tssoftgroup.tmobile.utils.URLEncoder;

public class Engine implements HTTPHandler {
	private static Engine instance;
	public boolean first = true;
	public PicInfo picInfo = new PicInfo();
	public DocumentInfo docInfo = new DocumentInfo();
	String showText;
	String loadedStatusText = "";
	HttpThread thread = new HttpThread(this);
	Vector statusVector = new Vector();
	int uploadingIndex = 0;
	public static final int MODE_VIEW_VIDEO_CONNECT = 1;
	public static final int MODE_VIEW_MCAST = 2;
	public static final int MODE_LOGIN = 3;
	public static final int MODE_ADD_COMMENT = 4;
	public static final int MODE_SEND_MORE_INFO = 5;
	public static final int MODE_VIEWTRAINING = 6;
	public static final int MODE_VIEWTRAINING_ANS = 7;
	public static final int MODE_ADD_TRAINING_RESULT = 8;
	public static final int MODE_VIEW_PROJECT = 9;
	public static final int MODE_VIEW_DOCUMENT = 10;
	public static final int MODE_VIEW_POLL = 11;
	public static final int MODE_VIEW_POLL_CHOICE = 12;
	public static final int MODE_INCREASE_POLL_COUNT = 13;
	public static final int MODE_SEND_EMAIL_DOC = 14;
	public static final int MODE_CHECK_VIDEO_CONNECT = 15;
	public static final int MODE_CHECK_MCAST = 16;
	public static final int MODE_CHECK_TRAINING = 17;
	public static boolean isLogin = false;
	public static String userId = "";
	public static PicInfo commnetPicInfo;
	private Vector bitmapHeader = new Vector();

	TrainingInfo trainingInfo;
	PollInfo pollInfo;
	public static Comment comment;
	int returnItem = 0;
	// 
	public static Vector allVideoConnect = new Vector();
	public static Vector allMcast = new Vector();
	public static Vector allTraining = new Vector();
	
	public HttpDownloadVideoThread downloadVideoThread  = new HttpDownloadVideoThread();
	private Vector downloadingVector = new Vector();
	public void registerStatus(BitmapFieldWithStatus bmp) {
		bitmapHeader.addElement(bmp);
	}
	public void addDownloadingImmediatly(DownloadCombiner combiner){
		downloadingVector.addElement(combiner);
	}
	public void removeDownloadingImmediatly(DownloadCombiner combiner){
		try{
		downloadingVector.removeElement(combiner);
		}catch(Exception e){
			
		}
	}
	public boolean isVideoDownloadingImmediately(String filename){
		boolean have = false;
		for (int i = 0; i <downloadingVector.size(); i++) {
			DownloadCombiner combiner = (DownloadCombiner)downloadingVector.elementAt(i);
			System.out.println("combiner.fileName" + combiner.fileName);
			System.out.println("filename" + filename);
			
			if(combiner.fileName.equals(filename)){
				have = true;
			}
		}
		return have;
	}
	public DownloadCombiner getVideoDownloadingImmediately(String filename){
		for (int i = 0; i <downloadingVector.size(); i++) {
			DownloadCombiner combiner = (DownloadCombiner)downloadingVector.elementAt(i);
			System.out.println("combiner.fileName" + combiner.fileName);
			System.out.println("filename" + filename);
			
			if(combiner.fileName.equals(filename)){
				return combiner;
			}
		}
		return null;
	}
	public void updateStatus(String status) {
		// for (int i = 0; i < bitmapHeader.size(); i++) {
		// try {
		// BitmapFieldWithStatus bmpfield = (BitmapFieldWithStatus) bitmapHeader
		// .elementAt(i);
		// bmpfield.setStatus(status);
		// } catch (Exception e) {
		// System.out.println("error update status ");
		// }
		// }
		Status.show(status);
	}

	public static Engine getInstance() {
		if (instance == null) {
			instance = new Engine();
			instance.thread.start();
			instance.downloadVideoThread.start();
		}
		return instance;
	}
	public void addDownloadVideo(DownloadCombiner combiner){
		downloadVideoThread.setTask(combiner);
		downloadVideoThread.go();
	}
	
	public void finishCallback(String result, int mode) {
		if (mode == MODE_VIEW_VIDEO_CONNECT || mode == MODE_CHECK_VIDEO_CONNECT) {
			try {
				JSONObject json = new JSONObject(result);
				JSONArray array = null;
				String status = json.optString("Status", "400");
				if (status.equals("400") && mode != MODE_CHECK_VIDEO_CONNECT) {
					final VideoConnectScreen videoShowScreen = VideoConnectScreen
							.getInstance();
					UiApplication.getUiApplication().invokeLater(
							new Runnable() {

								public void run() {
									if (WaitScreen.getInstance().isDisplayed()) {
										UiApplication.getUiApplication()
												.popScreen(
														WaitScreen
																.getInstance());
									}
									if (!videoShowScreen.isDisplayed()) {
										UiApplication.getUiApplication()
												.pushScreen(videoShowScreen);
									}
								}
							});
					return;
				}
				array = json.optJSONArray("videos");
				String numItem = json.optString("NumItem", "no such key");
				try {
					returnItem = Integer.parseInt(numItem);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Vector items = new Vector();
				for (int i = 0; i < array.length(); i++) {
					PicInfo picInfo = new PicInfo();
					JSONObject item = new JSONObject(array.getString(i));
					picInfo.setMCast(false);
					picInfo.setId(item.optString("id", "no such key"));
					picInfo.setTitle(item.optString("title", "no such key"));
					picInfo.setDescription(item.optString("description",
							"no such key"));
					picInfo.setVideoUrl(item.optString("filename_converted",
							"no such key"));
					picInfo.setDuration(item.optString("duration",
							"no such key"));
					picInfo.setThumbnailURL(item.optString("thumbnail",
							"no such key"));
					// fix thumbnail url
					// picInfo.setThumbnailURL("http://www.dhlknowledge.com/web/uploads/sample.png");
					// Begin Comment
					Vector comments = new Vector();
					try {
						JSONArray commentarray = item.optJSONArray("Comments");
						if (commentarray != null) {
							for (int j = 0; j < commentarray.length(); j++) {
								JSONObject attach = new JSONObject(commentarray
										.getString(j));
								Comment comment = new Comment();
								comment.setComment(attach.optString("comment",
										"no such key"));
								comment.setTime(attach.optString("time",
										"no such key"));
								comment.setUser(attach.optString("name",
										"no such key"));
								comments.addElement(comment);
							}
						}
					} catch (Exception e2) {
						System.out.println("Video attach is null");
					}
					picInfo.comments = comments;
					// End Comment
					items.addElement(picInfo);
				}
				// <=----------------- check video connect
				if (mode == MODE_CHECK_VIDEO_CONNECT) {
					allVideoConnect = items;
					checkWithVideos(allVideoConnect);
					return;
				}

				final VideoConnectScreen videoShowScreen = VideoConnectScreen
						.getInstance();
				final Vector myItems = items;
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {

						videoShowScreen.setList(myItems);
						videoShowScreen.processHaveNext(returnItem);
					}
				});
				LoadPicThread loadPicThread = new LoadPicThread(items,
						videoShowScreen);
				videoShowScreen.loader = loadPicThread;
				new Thread(loadPicThread).start();

				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						if (WaitScreen.getInstance().isDisplayed()) {
							UiApplication.getUiApplication().popScreen(
									WaitScreen.getInstance());
						}
						if (!videoShowScreen.isDisplayed()) {
							UiApplication.getUiApplication().pushScreen(
									videoShowScreen);
						}
					}
				});

			} catch (JSONException e) {
				System.out.println("Error Parsing JSON");
			} catch (Exception ex) {
				System.out.println("Otehr " + ex.toString());
			}
		} else if (mode == MODE_VIEW_MCAST || mode == MODE_CHECK_MCAST) {
			try {
				JSONObject json = new JSONObject(result);
				JSONArray array = null;
				String status = json.optString("Status", "400");
				if (status.equals("400") && mode != MODE_CHECK_MCAST) {
					final MCastScreen videoShowScreen = MCastScreen
							.getInstance();
					UiApplication.getUiApplication().invokeLater(
							new Runnable() {

								public void run() {
									if (WaitScreen.getInstance().isDisplayed()) {
										UiApplication.getUiApplication()
												.popScreen(
														WaitScreen
																.getInstance());
									}
									if (!videoShowScreen.isDisplayed()) {
										UiApplication.getUiApplication()
												.pushScreen(videoShowScreen);
									}
								}
							});
					return;
				}
				array = json.optJSONArray("videos");
				String numItem = json.optString("NumItem", "no such key");
				try {
					returnItem = Integer.parseInt(numItem);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Vector items = new Vector();
				for (int i = 0; i < array.length(); i++) {
					PicInfo picInfo = new PicInfo();
					JSONObject item = new JSONObject(array.getString(i));
					item.optString("id", "no such key");
					picInfo.setMCast(true);
					picInfo.setId(item.optString("id", "no such key"));
					picInfo.setTitle(item.optString("title", "no such key"));
					picInfo.setDescription(item.optString("description",
							"no such key"));
					picInfo.setVideoUrl(item.optString("filename_converted",
							"no such key"));
					picInfo.setDuration(item.optString("duration",
							"no such key"));
					picInfo.setThumbnailURL(item.optString("thumbnail",
							"no such key"));
					picInfo.setCat(item.optString("video_cat", "no such key"));//

					// fix thumbnail url
					// picInfo.setThumbnailURL("http://www.dhlknowledge.com/web/uploads/sample.png");
					// Video Attach
					Vector videoAttaches = new Vector();
					try {
						JSONArray vdotacharray = item
								.optJSONArray("VideoAttachs");
						if (vdotacharray != null) {
							for (int j = 0; j < vdotacharray.length(); j++) {
								JSONObject attach = new JSONObject(vdotacharray
										.getString(j));
								MoreInfo moreInfo = new MoreInfo();
								moreInfo.setTitle(attach.optString("title",
										"no such key"));
								moreInfo.setID(attach.optString("id",
										"no such key"));
								videoAttaches.addElement(moreInfo);
							}
						}
					} catch (Exception e2) {
						System.out.println("Video attach is null");
					}
					picInfo.moreInfos = videoAttaches;
					// End Video Attach
					// Comment
					Vector comments = new Vector();
					try {
						JSONArray commentarray = item.optJSONArray("Comments");
						if (commentarray != null) {
							for (int j = 0; j < commentarray.length(); j++) {
								JSONObject attach = new JSONObject(commentarray
										.getString(j));
								Comment comment = new Comment();
								comment.setComment(attach.optString("comment",
										"no such key"));
								comment.setTime(attach.optString("time",
										"no such key"));
								comment.setUser(attach.optString("name",
										"no such key"));
								comments.addElement(comment);
							}
						}
					} catch (Exception e2) {
						System.out.println("Video attach is null");
					}
					picInfo.comments = comments;
					// End Comment
					if (!picInfo.getCat().equals("")) {
						items.addElement(picInfo);
					}
				}
				// ///
				if (mode == MODE_CHECK_MCAST) {
					allMcast = items;
					checkWithVideos(allMcast);
					return;
				}
				// /
				final MCastScreen videoShowScreen = MCastScreen.getInstance();
				final Vector myItems = items;
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {

						videoShowScreen.setList(myItems, null);
						videoShowScreen.processHaveNext(returnItem);
					}
				});
				LoadPicThread loadPicThread = new LoadPicThread(items,
						videoShowScreen);
				videoShowScreen.loader = loadPicThread;
				new Thread(loadPicThread).start();
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						if (WaitScreen.getInstance().isDisplayed()) {
							UiApplication.getUiApplication().popScreen(
									WaitScreen.getInstance());
						}
						if (!videoShowScreen.isDisplayed()) {
							UiApplication.getUiApplication().pushScreen(
									videoShowScreen);
						}
					}
				});
			} catch (JSONException e) {
				System.out.println("Error Parsing JSON");
			} catch (Exception ex) {
				System.out.println("Otehr " + ex.toString());
			}
		} else if (mode == MODE_LOGIN) {
			System.out.print("result " + mode);
			if (result.equals("NOCONNECTION")) {
				// UiApplication.getUiApplication().invokeLater(new Runnable(){
				//
				// public void run() {
				// Dialog.alert("Cannot connect to internet. Please check your internet connection");
				// }});
				return;
			}
			try {
				JSONObject json = new JSONObject(result);
				String status = json.optString("Status", "400");
				if (status.equals("200")) {
					String user = json.optString("user", "no such key");
					JSONObject userJson = new JSONObject(user);
					Engine.userId = userJson.optString("id", "2200");
					ProfileEntry profile = ProfileEntry.getInstance();
					profile.name = userJson.optString("name", "no such key");
					profile.email = userJson.optString("email", "no such key");
					profile.mobile = userJson
							.optString("mobile", "no such key");
					UiApplication.getUiApplication().invokeLater(
							new Runnable() {

								public void run() {
									CrieUtils.removeCurrent();
									UiApplication
											.getUiApplication()
											.pushScreen(
													com.tssoftgroup.tmobile.screen.MyMainScreen
															.getInstance());
								}
							});

				} else {
					UiApplication.getUiApplication().invokeLater(
							new Runnable() {

								public void run() {
									CrieUtils.removeCurrent();
									Dialog.alert("Wrong Passcode");
								}
							});
				}
			} catch (JSONException e) {
				e.printStackTrace();
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						CrieUtils.removeCurrent();
						Dialog.alert("Wrong Passcode");
					}
				});

			}
		} else if (mode == MODE_ADD_COMMENT) {
			UiApplication.getUiApplication().invokeLater(new Runnable() {

				public void run() {
					Screen current = UiApplication.getUiApplication()
							.getActiveScreen();
					if (current instanceof MCastDetail) {
						UiApplication.getUiApplication().popScreen(current);
						UiApplication.getUiApplication().pushScreen(
								new MCastDetail(Engine.commnetPicInfo));
						Dialog.alert("Your Comment is sent");
					}
					if (current instanceof MCastPlayerScreen) {
						// UiApplication.getUiApplication().popScreen(current);
						// MCastPlayerScreen scr = new MCastPlayerScreen(
						// Engine.commnetPicInfo);
						// scr.addCommentMoreInfo();
						// scr.isAlreadyAddComment = true;
						// scr.commentLabelField.setFocus();
						// UiApplication.getUiApplication().pushScreen(scr);

						MCastPlayerScreen cur = (MCastPlayerScreen) current;
						// UiApplication.getUiApplication().popScreen(current);
						// MCastPlayerScreen scr = new MCastPlayerScreen(
						// Engine.commnetPicInfo);
						cur.picinfo = Engine.commnetPicInfo;
						// cur.addComment();
						cur.isAlreadyAddComment = true;
						// cur.commentLabelField.setFocus();
						// UiApplication.getUiApplication().pushScreen(scr);
						// 
						String choices[] = { "OK" };
						int values[] = { Dialog.OK };
						Dialog dia = new Dialog("Your Comment is sent",
								choices, values, Dialog.OK,
								Bitmap.getPredefinedBitmap(Bitmap.INFORMATION));
						dia.doModal();
					}
					if (current instanceof VideoConnectDetail) {
						UiApplication.getUiApplication().popScreen(current);
						UiApplication.getUiApplication().pushScreen(
								new VideoConnectDetail(Engine.commnetPicInfo));
						Dialog.alert("Your Comment is sent");
					}
					if (current instanceof VideoConnectPlayerScreen) {
						// UiApplication.getUiApplication().popScreen(current);
						// VideoConnectPlayerScreen scr = new
						// VideoConnectPlayerScreen(
						// Engine.commnetPicInfo);
						// scr.addCommentMoreInfo();
						// scr.isAlreadyAddComment = true;
						// scr.commentLabelField.setFocus();
						// UiApplication.getUiApplication().pushScreen(scr);
						VideoConnectPlayerScreen cur = (VideoConnectPlayerScreen) current;
						// UiApplication.getUiApplication().popScreen(current);
						// MCastPlayerScreen scr = new MCastPlayerScreen(
						// Engine.commnetPicInfo);
						cur.picinfo = Engine.commnetPicInfo;
						cur.isAlreadyAddComment = true;
						// 
						String choices[] = { "OK" };
						int values[] = { Dialog.OK };
						Dialog dia = new Dialog("Your Comment is sent",
								choices, values, Dialog.OK,
								Bitmap.getPredefinedBitmap(Bitmap.INFORMATION));
						dia.doModal();
					}

				}
			});
		} else if (mode == MODE_SEND_MORE_INFO) {
			UiApplication.getUiApplication().invokeLater(new Runnable() {

				public void run() {
					String choices[] = { "OK" };
					int values[] = { Dialog.OK };
					Dialog dia = new Dialog("This file has been sent to "
							+ ProfileEntry.getInstance().email, choices,
							values, Dialog.OK, Bitmap
									.getPredefinedBitmap(Bitmap.INFORMATION));
					dia.doModal();
				}
			});
		} else if (mode == MODE_VIEWTRAINING || mode == MODE_CHECK_TRAINING) {

			try {
				JSONObject json = new JSONObject(result);
				JSONArray array = null;
				array = json.optJSONArray("training");
				String numItem = json.optString("NumItem", "no such key");
				try {
					returnItem = Integer.parseInt(numItem);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Vector items = new Vector();
				for (int i = 0; i < array.length(); i++) {
					TrainingInfo trainInfo = new TrainingInfo();
					JSONObject item = new JSONObject(array.getString(i));
					trainInfo.setId(item.optString("id", "no such key"));
					trainInfo.setTitle(item.optString("title", "no such key"));
					trainInfo.setDescription(item.optString("description",
							"no such key"));
					trainInfo.setExplanation(item.optString("explanation",
							"no such key"));
					String filename = item.optString("filename", "no such key");
					if (filename != null && filename.indexOf("/") == 0) {
						trainInfo.setVideoUrl(filename.substring(1));
					} else {
						trainInfo.setVideoUrl(filename);

					}
					trainInfo.setThumbnailUrl(item.optString("thumbnail",
							"no such key"));
					trainInfo.setCat(item.optString("training_cat",
							"no such key"));//
					if (!trainInfo.getCat().equals("")) {
						items.addElement(trainInfo);
					}
				}
				// ///
				if (mode == MODE_CHECK_TRAINING) {
					allTraining = items;
					checkWithVideos(allTraining);
					return;
				}
				// /
				final TrainingListScreen trainingScreen = TrainingListScreen
						.getInstance();
				final Vector myItems = items;
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {

						trainingScreen.setList(myItems, null);
						trainingScreen.processHaveNext(returnItem);
					}
				});
				LoadPicThread loadPicThread = new LoadPicThread(items,
						trainingScreen);
				trainingScreen.loader = loadPicThread;
				new Thread(loadPicThread).start();
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						if (WaitScreen.getInstance().isDisplayed()) {
							UiApplication.getUiApplication().popScreen(
									WaitScreen.getInstance());
						}
						if (!trainingScreen.isDisplayed()) {
							UiApplication.getUiApplication().pushScreen(
									trainingScreen);
						}
					}
				});
			} catch (JSONException e) {
				System.out.println("Error Parsing JSON");
			} catch (Exception ex) {
				System.out.println("Otehr " + ex.toString());
			}

		} else if (mode == MODE_VIEWTRAINING_ANS) {

			try {
				trainingInfo.questions = new Vector();
				JSONObject json = new JSONObject(result);
				JSONArray array = null;
				array = json.optJSONArray("training_ans");
				Vector raws = new Vector();
				int numQuestion = 0;
				for (int i = 0; i < array.length(); i++) {
					JSONObject item = new JSONObject(array.getString(i));
					RawTrainingAns ans = new RawTrainingAns();
					ans.choiceChar = item.optString("choice_char",
							"no such key");
					ans.str = item.optString("str", "no such key");
					ans.item = item.optString("item", "no such key");
					ans.type = item.optString("type", "no such key");

					try {
						int itemInt = Integer.parseInt(ans.item);
						if (itemInt > numQuestion) {
							numQuestion = itemInt;
						}
					} catch (Exception e) {

					}
					raws.addElement(ans);
				}
				// / process Raw data
				// find number of question
				Question firstQuestion = new Question();
				;
				for (int i = 0; i < numQuestion; i++) {
					Question question = new Question();
					question.numQuestion = i + 1;
					if (question.numQuestion == 1) {
						firstQuestion = question;
					}
					for (int j = 0; j < raws.size(); j++) {
						RawTrainingAns raw = (RawTrainingAns) raws.elementAt(j);
						if ((question.numQuestion + "").equals(raw.item)) {
							if (raw.type.equals("0")) {
								question.question = raw.str;
							}
							if (raw.type.equals("1")) {
								Choice ch = new Choice();
								ch.key = raw.choiceChar;
								ch.value = raw.str;
								if (!ch.value.equals("")) {
									question.choices.addElement(ch);
								}
							}
							if (raw.type.equals("2")
									&& question.answer.equals("")) {
								question.answer = raw.choiceChar;
							}
						}
					}
					trainingInfo.questions.addElement(question);
				}
				String[] questions = composeRadioFromQuestion(firstQuestion);
				// { firstQuestion, "b. O", "c. A", "d. OK" };
				final TrainingQuestionScreen screen = new TrainingQuestionScreen(
						trainingInfo);
				screen.setQuestion(firstQuestion.question);
				screen.setRadio(questions);
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						CrieUtils.removeCurrent();
						UiApplication.getUiApplication().pushScreen(screen);
					}
				});

			} catch (JSONException e) {
				System.out.println("Error Parsing JSON");
			} catch (Exception ex) {
				System.out.println("Otehr " + ex.toString());
			}

		} else if (mode == MODE_ADD_TRAINING_RESULT) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			UiApplication.getUiApplication().invokeLater(new Runnable() {

				public void run() {

					WaitScreen.getInstance().setText("Waitting");
					CrieUtils.removeCurrent();
					CrieUtils.removeCurrent();
					CrieUtils.removeCurrent();

				}
			});
		} else if (mode == MODE_VIEW_PROJECT) {

			try {
				JSONObject json = new JSONObject(result);
				JSONArray array = null;
				array = json.optJSONArray("project");
				String numItem = json.optString("NumItem", "no such key");
				try {
					returnItem = Integer.parseInt(numItem);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Vector items = new Vector();
				for (int i = 0; i < array.length(); i++) {
					ProjectInfo project = new ProjectInfo();
					JSONObject item = new JSONObject(array.getString(i));
					project.setId(item.optString("project_id", "no such key"));
					project.setProjectName(item.optString("project_name",
							"no such key"));
					project.setProjectDesc(item.optString("project_desc",
							"no such key"));
					Vector members = new Vector();
					try {
						JSONArray contactArray = item.optJSONArray("contacts");
						if (contactArray != null) {
							for (int j = 0; j < contactArray.length(); j++) {
								JSONObject contact = new JSONObject(
										contactArray.getString(j));
								User user = new User();
								user.setName(contact.optString("name",
										"no such key"));
								user.setEmail(contact.optString("email",
										"no such key"));
								user.setMobile(contact.optString("mobile",
										"no such key"));
								user.setPhone(contact.optString("phone",
										"no such key"));
								user.setPassCode(contact.optString("passcode",
										"no such key"));
								user.setId(contact.optString("user_id",
										"no such key"));
								user.setPosition(contact.optString("position",
										""));
								members.addElement(user);
							}
						}
					} catch (Exception e2) {
						System.out.println("Video attach is null");
					}
					project.setUsers(members);
					items.addElement(project);
				}

				final ProjectListScreen projectScreen = ProjectListScreen
						.getInstance();
				final Vector myItems = items;
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {

						projectScreen.setList(myItems);
						projectScreen.processHaveNext(returnItem);
					}
				});
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						if (WaitScreen.getInstance().isDisplayed()) {
							UiApplication.getUiApplication().popScreen(
									WaitScreen.getInstance());
						}
						if (!projectScreen.isDisplayed()) {
							UiApplication.getUiApplication().pushScreen(
									projectScreen);
						}
					}
				});
			} catch (JSONException e) {
				System.out.println("Error Parsing JSON");
			} catch (Exception ex) {
				System.out.println("Otehr " + ex.toString());
			}
		} else if (mode == MODE_VIEW_DOCUMENT) {

			try {
				JSONObject json = new JSONObject(result);
				JSONArray array = null;
				array = json.optJSONArray("document_share");
				String numItem = json.optString("NumItem", "no such key");
				try {
					returnItem = Integer.parseInt(numItem);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Vector items = new Vector();
				for (int i = 0; i < array.length(); i++) {
					DocumentInfo docInfo = new DocumentInfo();
					JSONObject item = new JSONObject(array.getString(i));
					docInfo.setId(item.optString("id", "no such key"));
					docInfo.setFileSize(item.optString("size", "no such key"));
					docInfo.setDescription(item.optString("description",
							"no such key"));
					docInfo.setFileName(item.optString("filename",
							"no such key"));
					String fileName = item.optString("filename", "no such key");

					if (fileName.length() > 0 && fileName.charAt(0) == '/') {
						fileName = fileName.substring(1);
					}
					fileName = StringUtil.urlEncode(fileName);
					docInfo.setHTTPfilePath(Const.DOCUMENT_PATH + fileName);
					docInfo.setProductInfo(item.optString("title",
							"no such key"));
					items.addElement(docInfo);
				}
				final DocumentListScreen docScreen = DocumentListScreen
						.getInstance();
				final Vector myItems = items;
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {

						docScreen.setList(myItems);
						docScreen.processHaveNext(returnItem);
					}
				});
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						if (WaitScreen.getInstance().isDisplayed()) {
							UiApplication.getUiApplication().popScreen(
									WaitScreen.getInstance());
						}
						if (!docScreen.isDisplayed()) {
							UiApplication.getUiApplication().pushScreen(
									docScreen);
						}
					}
				});
			} catch (JSONException e) {
				System.out.println("Error Parsing JSON");
			} catch (Exception ex) {
				System.out.println("Otehr " + ex.toString());
			}
		} else if (mode == MODE_VIEW_POLL) {

			try {
				JSONObject json = new JSONObject(result);
				JSONArray array = null;
				array = json.optJSONArray("poll");
				String numItem = json.optString("NumItem", "no such key");
				try {
					returnItem = Integer.parseInt(numItem);
				} catch (Exception e) {
					e.printStackTrace();
				}
				Vector items = new Vector();
				for (int i = 0; i < array.length(); i++) {
					PollInfo pollInfo = new PollInfo();
					JSONObject item = new JSONObject(array.getString(i));
					pollInfo.setId(item.optString("id", "no such key"));
					pollInfo.setTitle(item.optString("title", "no such key"));
					pollInfo.setDescription(item.optString("description",
							"no such key"));
					items.addElement(pollInfo);
				}
				final PollListScreen pollScreen = PollListScreen.getInstance();
				final Vector myItems = items;
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {

						pollScreen.setList(myItems);
						pollScreen.processHaveNext(returnItem);
					}
				});
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						if (WaitScreen.getInstance().isDisplayed()) {
							UiApplication.getUiApplication().popScreen(
									WaitScreen.getInstance());
						}
						if (!pollScreen.isDisplayed()) {
							UiApplication.getUiApplication().pushScreen(
									pollScreen);
						}
					}
				});
			} catch (JSONException e) {
				System.out.println("Error Parsing JSON");
			} catch (Exception ex) {
				System.out.println("Otehr " + ex.toString());
			}
		} else if (mode == MODE_VIEW_POLL_CHOICE) {
			try {
				pollInfo.questions = new Vector();
				JSONObject json = new JSONObject(result);
				JSONArray array = null;
				array = json.optJSONArray("poll_ans");
				Vector raws = new Vector();
				int numQuestion = 0;
				for (int i = 0; i < array.length(); i++) {
					JSONObject item = new JSONObject(array.getString(i));
					RawTrainingAns ans = new RawTrainingAns();
					ans.choiceChar = item.optString("choice_char",
							"no such key");
					ans.str = item.optString("str", "no such key");
					ans.item = item.optString("item", "no such key");
					ans.type = item.optString("type", "no such key");
					try {
						ans.count = Integer.parseInt(item.optString("count",
								"no such key"));
					} catch (Exception e) {

					}
					try {
						int itemInt = Integer.parseInt(ans.item);
						if (itemInt > numQuestion) {
							numQuestion = itemInt;
						}
					} catch (Exception e) {

					}
					raws.addElement(ans);
				}
				// / process Raw data
				// find number of question
				Question firstQuestion = new Question();
				for (int i = 0; i < numQuestion; i++) {
					Question question = new Question();
					question.numQuestion = i + 1;
					if (question.numQuestion == 1) {
						firstQuestion = question;
					}
					for (int j = 0; j < raws.size(); j++) {
						RawTrainingAns raw = (RawTrainingAns) raws.elementAt(j);
						if ((question.numQuestion + "").equals(raw.item)) {
							if (raw.type.equals("0")) {
								question.question = raw.str;
							}
							if (raw.type.equals("1")) {
								Choice ch = new Choice();
								ch.key = raw.choiceChar;
								ch.value = raw.str;
								ch.count = raw.count;
								question.choices.addElement(ch);
							}
						}
					}
					pollInfo.questions.addElement(question);
				}
				String[] questions = composeRadioFromQuestion(firstQuestion);
				// { firstQuestion, "b. O", "c. A", "d. OK" };
				final PollQuestionScreen screen = new PollQuestionScreen(
						pollInfo);
				screen.setQuestion(firstQuestion.question);
				screen.setRadio(questions);
				UiApplication.getUiApplication().invokeLater(new Runnable() {

					public void run() {
						CrieUtils.removeCurrent();
						UiApplication.getUiApplication().pushScreen(screen);
					}
				});

			} catch (JSONException e) {
				System.out.println("Error Parsing JSON");
			} catch (Exception ex) {
				System.out.println("Otehr " + ex.toString());
			}

		} else if (mode == MODE_SEND_EMAIL_DOC) {
			UiApplication.getUiApplication().invokeLater(new Runnable() {
				public void run() {
					Dialog.alert("This file has been sent to "
							+ ProfileEntry.getInstance().email);
				}
			});
		}
	}

	public static String[] composeRadioFromQuestion(Question question) {
		String[] questions = new String[question.choices.size()];
		for (int i = 0; i < questions.length; i++) {
			Choice c = (Choice) question.choices.elementAt(i);
			questions[i] = c.key + ". " + c.value;
		}
		return questions;
	}

	public void sendVideo() {

		UploadThread uploadThread = UploadThread.getInstance();
		System.out.println("Before upload");
		uploadThread.upLoad(picInfo);
		System.out.println("After upload");
		statusVector.addElement(picInfo);
		UiApplication.getUiApplication().invokeLater(new Runnable() {

			public void run() {
				Engine.getInstance().updateStatus(
						"Uploading (" + uploadingIndex + "/"
								+ statusVector.size() + ")");
				// Status.show("Sending video (" + uploadingIndex + "/"
				// + statusVector.size() + ")... Please wait");
				// MainAppScreen.getInstance().setUploadingNum(
				// uploadingIndex+"/" + statusVector.size());
			}
		});
		picInfo = new PicInfo();
		// System.out
		// .println("hhhh"
		// + UiApplication.getUiApplication().getActiveScreen()
		// .getClass());

	}

	public void sendDocument() {

		UploadThreadDoc uploadThread = UploadThreadDoc.getInstance();
		System.out.println("Before upload");
		uploadThread.upLoad(docInfo);
		System.out.println("After upload");
		statusVector.addElement(docInfo);
		UiApplication.getUiApplication().invokeLater(new Runnable() {

			public void run() {
				Engine.getInstance().updateStatus(
						"Uploading (" + uploadingIndex + "/"
								+ statusVector.size() + ")");
				// Status.show("Sending video (" + uploadingIndex + "/"
				// + statusVector.size() + ")... Please wait");
				// MainAppScreen.getInstance().setUploadingNum(
				// uploadingIndex+"/" + statusVector.size());
			}
		});
		docInfo = new DocumentInfo();
		// System.out
		// .println("hhhh"
		// + UiApplication.getUiApplication().getActiveScreen()
		// .getClass());

	}

	// start from 0
	public void viewVideoConnect(int start, String search) {

		// WaitScreen waitScr = WaitScreen.getInstance();
		// waitScr.setText("Logging in");
		// if (!waitScr.isDisplayed()) {
		// UiApplication.getUiApplication().pushScreen(waitScr);
		// }
		String url = Const.URL_VIEW_VIDEO;
		String body = "type=1&start=" + start + "&num=" + Const.NUM_LIST
				+ "&search=" + search;
		url = Const.URL_VIEW_VIDEO;
		thread.setTask(url, body, MODE_VIEW_VIDEO_CONNECT);
		thread.go();
	}

	public void viewVideoMCast(int start, String search) {

		// WaitScreen waitScr = WaitScreen.getInstance();
		// waitScr.setText("Logging in");
		// if (!waitScr.isDisplayed()) {
		// UiApplication.getUiApplication().pushScreen(waitScr);
		// }
		String url = Const.URL_VIEW_VIDEO;
		String body = "type=0&start=" + start + "&num=" + Const.NUM_LIST
				+ "&search=" + search;
		thread.setTask(url, body, MODE_VIEW_MCAST);
		thread.go();
	}

	public void login(String email, String passcode) {
		String url = Const.URL_LOGIN;
		String body = "passcode=" + passcode;
		thread.setTask(url, body, MODE_LOGIN);
		thread.go();
	}

	public void addComment(String videoId, String userId, String comment,
			boolean isMcast) {
		// URL Encode comment
		try {
			comment = URLEncoder.encode(comment);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String url = isMcast ? Const.URL_COMMENT_MCAST
				: Const.URL_COMMENT_VIDEOCONNECT;
		String body = "comment=" + comment + "&videoid=" + videoId + "&uid="
				+ userId;
		thread.setTask(url, body, MODE_ADD_COMMENT);
		thread.go();
	}

	public void sendMoreInfo(String moreinfoId) {
		// URL Encode comment
		String url = Const.URL_SEND_MOREINFO_EMAIL;
		String body = "attachid=" + moreinfoId + "&uid=" + Engine.userId;
		thread.setTask(url, body, MODE_SEND_MORE_INFO);
		thread.go();
	}

	public void getTraining(int start, String search) {
		// URL Encode comment
		String url = Const.URL_VIEW_TRAINING;
		String body = "start=" + start + "&num=" + Const.NUM_LIST + "&search="
				+ search;
		thread.setTask(url, body, MODE_VIEWTRAINING);
		thread.go();
	}

	public void getTrainingAns(TrainingInfo info) {
		this.trainingInfo = info;
		// URL Encode comment
		String url = Const.URL_VIEW_TRAINING_ANS;
		String body = "training_id=" + trainingInfo.getId();
		thread.setTask(url, body, MODE_VIEWTRAINING_ANS);
		thread.go();
	}

	public void addTrainingResult(TrainingInfo info, int score, int fullScore) {
		this.trainingInfo = info;
		// URL Encode comment
		String url = Const.URL_ADD_TRAINING_RESULT;
		String body = "uid=" + Engine.userId + "&trainingid="
				+ trainingInfo.getId() + "&score=" + score + "&fullscore="
				+ fullScore;
		thread.setTask(url, body, MODE_ADD_TRAINING_RESULT);
		thread.go();
	}

	public void getProject(int start, String search) {
		// URL Encode comment
		String url = Const.URL_VIEW_PROJECT;
		String body = "start=" + start + "&num=" + Const.NUM_LIST + "&search="
				+ search;
		thread.setTask(url, body, MODE_VIEW_PROJECT);
		thread.go();
	}

	public void getDocument(int start, String search) {
		// URL Encode comment
		String url = Const.URL_VIEW_DOCUMENT;
		String body = "start=" + start + "&num=" + Const.NUM_LIST + "&search="
				+ search;
		thread.setTask(url, body, MODE_VIEW_DOCUMENT);
		thread.go();
	}

	public void getPoll(int start, String search) {
		// URL Encode comment
		String url = Const.URL_VIEW_POLL;
		String body = "start=" + start + "&num=" + Const.NUM_LIST + "&search="
				+ search;
		thread.setTask(url, body, MODE_VIEW_POLL);
		thread.go();
	}

	public void getPollChoice(PollInfo pollInfo) {
		// URL Encode comment
		this.pollInfo = pollInfo;
		String body = "poll_id=" + pollInfo.getId();
		String url = Const.URL_VIEW_POLL_CHOICE;
		thread.setTask(url, body, MODE_VIEW_POLL_CHOICE);
		thread.go();
	}

	public void increasePollCount(String pollId, String itemId, String chr) {
		String body = "poll_id=" + pollId + "&item_id=" + itemId + "&char="
				+ chr;
		String url = Const.URL_INCREASE_POLL_COUNT;
		thread.setTask(url, body, MODE_INCREASE_POLL_COUNT);
		thread.go();
	}

	public void sendEmailDoc(String docId) {
		// URL Encode comment
		String url = Const.URL_EMAIL_DOC;
		String body = "document_id=" + docId + "&uid=" + Engine.userId;
		thread.setTask(url, body, MODE_SEND_EMAIL_DOC);
		thread.go();
	}

	public void checkVideoConnect() {
		System.out.println("check Video Connetc");
		// WaitScreen waitScr = WaitScreen.getInstance();
		// waitScr.setText("Logging in");
		// if (!waitScr.isDisplayed()) {
		// UiApplication.getUiApplication().pushScreen(waitScr);
		// }
		String url = Const.URL_VIEW_VIDEO;
		String body = "type=1";
		url = Const.URL_VIEW_VIDEO;
		thread.setTask(url, body, MODE_CHECK_VIDEO_CONNECT);
		thread.go();
	}

	public void checkMCast() {
		System.out.println("check mcast");
		// WaitScreen waitScr = WaitScreen.getInstance();
		// waitScr.setText("Logging in");
		// if (!waitScr.isDisplayed()) {
		// UiApplication.getUiApplication().pushScreen(waitScr);
		// }
		String url = Const.URL_VIEW_VIDEO;
		String body = "type=0";
		thread.setTask(url, body, MODE_CHECK_MCAST);
		thread.go();
	}

	public void checkTraining() {
		System.out.println("check trining");
		// URL Encode comment
		String url = Const.URL_VIEW_TRAINING;
		String body = "";
		thread.setTask(url, body, MODE_CHECK_TRAINING);
		thread.go();
	}

	private void checkWithVideos(Vector items) {
		System.out.println("check With Video " + items.size());
		// Get All video in profile Entry
		ProfileEntry profile = ProfileEntry.getInstance();
		Vector videos = Video.convertStringToVector(profile.videos);
		for (int i = 0; i < items.size(); i++) {
			Object item = items.elementAt(i);
			String fileName = "";
			String videoName = "";
			if (item instanceof PicInfo) {
				PicInfo picinfo = (PicInfo) item;
				fileName = picinfo.getFilename();
				videoName = picinfo.getTitle();
			}
			if (item instanceof TrainingInfo) {
				TrainingInfo trainInfo = (TrainingInfo) item;
				fileName = trainInfo.getFilename();
				videoName = trainInfo.getTitle();
			}
			boolean alreadyHaveVideo = false;
			for (int j = 0; j < videos.size(); j++) {
				Video vid = (Video) videos.elementAt(j);
				if (vid.getName().equals(fileName)) {
					alreadyHaveVideo = true;
				}
			}
			if (!alreadyHaveVideo) {
				Video newVideo = new Video();
				newVideo.setName(fileName);
				newVideo.setPercent("0");
				newVideo.setScheduleTime(new Date().getTime() + "");
				newVideo.setStatus("1");
				newVideo.setTitle(videoName);
				videos.addElement(newVideo);
			}
		}
		profile.videos = Video.convertVectorToString(videos);
		profile.saveProfile();
	}
}

class CacheTimeLineItem {
	Vector timelineItem = new Vector();
	long time = 0;

	public CacheTimeLineItem(Vector timelineItem, long time) {
		this.timelineItem = timelineItem;
		this.time = time;
	}
}

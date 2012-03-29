package com.kii.demo.cloudstorage.activities;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kii.cloud.storage.utils.Utils;
import com.kii.demo.cloudstorage.R;

/**
 * 
 * Activity to handle all File operations. 
 * List Files and List Trash do not require a File ID.
 * Delete, restore, get File Info, get File Content requires a File ID
 * Upload a file, after successfully uploaded, sets File ID for the operations that need it.
 *
 */
public class FileOperationActivity extends Activity implements
		OnItemClickListener {
	private TextView mRightTitle;
	private TextView mLeftTitle;

	private static final int DIALOG_FILE_UPLOAD = 0;
	private static final int DIALOG_FILE_INFO = 1;
	private static final int DIALOG_FILE_DELETE = 2;
	private static final int DIALOG_FILE_RESTORE = 3;
	private static final int DIALOG_FILE_BODY = 4;
	private static final int DIALOG_FILE_SEARCH = 5;
	private static final int DIALOG_FILE_UPDATE = 6;

	private AlertDialog mProgressDialog;
//
//
//	@Override
//
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//		setContentView(R.layout.apidemo);
//		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
//
//		ListView list = (ListView) findViewById(R.id.listView1);
//		list.setAdapter(new ArrayAdapter<String>(this,
//				android.R.layout.simple_list_item_1, ConstantValues.FILE_ITEMS));
//		list.setOnItemClickListener(this);
//
//		mLeftTitle = (TextView) findViewById(R.id.left_text);
//		mRightTitle = (TextView) findViewById(R.id.right_text);
//
//		EasyClient.start(this, ConstantValues.APP_ID, ConstantValues.APP_KEY,
//				ConstantValues.KII_OBJECT_NAME, ConstantValues.VIRTUAL_ROOT);
//
//		mClient = EasyClient.getInstance();
//
//		updateUserStateTitle();
//	}
//
//	private void updateUserStateTitle() {
//		if (ConstantValues.mSyncMode) {
//			mLeftTitle.setText(R.string.sync_title);
//		} else {
//			mLeftTitle.setText(R.string.async_title);
//		}
//
//		if (mClient == null || mClient.getloginUser() == null) {
//			mRightTitle.setText("No User Login");
//		} else {
//			KiiUser user = mClient.getloginUser();
//			mRightTitle.setText("Login:" + user.getUsername());
//		}
//	}
//
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
//			long arg3) {
//		switch (position) {
//		case ConstantValues.ITEM_FILE_UPLOAD:
//			this.showDialog(DIALOG_FILE_UPLOAD);
//			break;
//		case ConstantValues.ITEM_FILE_LIST:
//			if (ConstantValues.mSyncMode) {
//				showListFiles();
//			} else {
//				int token = asyncListFiles();
//				if (token >= 0) {
//					showProcessing(token, "Listing files");
//				}
//			}
//			break;
//		case ConstantValues.ITEM_FILE_LIST_TRASH:
//			if (ConstantValues.mSyncMode) {
//				showListTrashFiles();
//			} else {
//				int token = asyncListTrashFiles();
//				if (token >= 0) {
//					showProcessing(token, "Listing trashed files");
//				}
//			}
//			break;
//		case ConstantValues.ITEM_FILE_INFO:
//			showDialog(DIALOG_FILE_INFO);
//			break;
//		case ConstantValues.ITEM_FILE_BODY:
//			showDialog(DIALOG_FILE_BODY);
//			break;
//		case ConstantValues.ITEM_FILE_DELETE:
//			showDialog(DIALOG_FILE_DELETE);
//			break;
//		case ConstantValues.ITEM_FILE_RESTORE:
//			showDialog(DIALOG_FILE_RESTORE);
//			break;
//		// case ConstantValues.ITEM_FILE_SEARCH:
//		// showDialog(DIALOG_FILE_SEARCH);
//		// break;
//		case ConstantValues.ITEM_FILE_EMPTY_TRASH:
//			if (ConstantValues.mSyncMode) {
//				emptyTrashcan();
//			} else {
//				int token = asyncEmptyTrashcan();
//
//				if (token >= 0) {
//					showProcessing(token, "Empty trashcan");
//				}
//			}
//			break;
//		case ConstantValues.ITEM_FILE_UPDATE:
//			showDialog(DIALOG_FILE_UPDATE);
//			break;
//		}
//
//	}
//
//	@Override
//	protected Dialog onCreateDialog(int id, Bundle args) {
//		LayoutInflater factory = LayoutInflater.from(this);
//		switch (id) {
//		case DIALOG_FILE_UPLOAD: {
//			final View uploadView = factory.inflate(
//					R.layout.alert_dialog_file_upload, null);
//			final TextView pathView = (TextView) uploadView
//					.findViewById(R.id.path_edit);
//			pathView.setText(ConstantValues.DEMO_FILE);
//			return new AlertDialog.Builder(FileOperationActivity.this)
//					.setIcon(R.drawable.alert_dialog_icon)
//					.setTitle(R.string.file_upload)
//					.setView(uploadView)
//					.setPositiveButton(R.string.alert_dialog_ok,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int whichButton) {
//									if (ConstantValues.mSyncMode) {
//										uploadFile(pathView.getText()
//												.toString());
//									} else {
//										int token = asyncUploadFile(pathView
//												.getText().toString());
//										showProcessing(token, "Uploading file");
//									}
//								}
//							})
//					.setNegativeButton(R.string.alert_dialog_cancel,
//							new DialogInterface.OnClickListener() {
//								public void onClick(DialogInterface dialog,
//										int whichButton) {
//
//									/* User clicked cancel so do some stuff */
//								}
//							}).create();
//		}
//		case DIALOG_FILE_INFO:
//			if (assertHasFile())
//				return showFileInfoDialog(factory);
//			break;
//		case DIALOG_FILE_DELETE:
//			if (assertHasFile())
//				return showDeleteFileDialog(factory);
//			break;
//		case DIALOG_FILE_RESTORE:
//			if (assertHasFile())
//				return showRestoreFileDialog(factory);
//			break;
//		case DIALOG_FILE_BODY:
//			if (assertHasFile())
//				return showFileBodyDialog(factory);
//			break;
//		case DIALOG_FILE_UPDATE:
//			if (assertHasFile())
//				return showFileUpdateDialog(factory);
//			break;
//		}
//		return null;
//	}
//
//	private Dialog showFileUpdateDialog(LayoutInflater factory) {
//		final View uploadView = factory.inflate(
//				R.layout.alert_dialog_file_update, null);
//		final TextView title = (TextView) uploadView
//				.findViewById(R.id.file_title);
//		final TextView optional = (TextView) uploadView
//				.findViewById(R.id.file_optional);
//		if (mKiiFile != null) {
//			title.setText(mKiiFile.getTitle());
//			optional.setText(mKiiFile.getOptional());
//		}
//		return new AlertDialog.Builder(FileOperationActivity.this)
//				.setIcon(R.drawable.alert_dialog_icon)
//				.setTitle(R.string.file_update)
//				.setView(uploadView)
//				.setPositiveButton(R.string.alert_dialog_ok,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//								if (ConstantValues.mSyncMode) {
//									updateFile(title.getText().toString(),
//											optional.getText().toString());
//								} else {
//									int token = asyncUpdateFile(title.getText()
//											.toString(), optional.getText()
//											.toString());
//									showProcessing(token, "Updating file");
//								}
//							}
//						})
//				.setNegativeButton(R.string.alert_dialog_cancel,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//
//								/* User clicked cancel so do some stuff */
//							}
//						}).create();
//	}
//
//	private boolean assertHasFile() {
//		if (mKiiFile != null)
//			return true;
//		showError(getResources().getString(R.string.file_empty));
//		return false;
//	}
//
//	private Dialog showFileInfoDialog(LayoutInflater factory) {
//		final View view = factory
//				.inflate(R.layout.alert_dialog_file_info, null);
//		final TextView idView = (TextView) view.findViewById(R.id.id_edit);
//		if (mKiiFile != null)
//			idView.setText(mKiiFile.getString("ID", null));
//		return new AlertDialog.Builder(FileOperationActivity.this)
//				.setIcon(R.drawable.alert_dialog_icon)
//				.setTitle(R.string.file_info)
//				.setView(view)
//				.setPositiveButton(R.string.alert_dialog_ok,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//								if (ConstantValues.mSyncMode) {
//									fileInfo(idView.getText().toString());
//								} else {
//									int token = asyncFileInfo(idView.getText()
//											.toString());
//									showProcessing(token, "Getting File Info");
//								}
//							}
//						})
//				.setNegativeButton(R.string.alert_dialog_cancel,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//
//								/* User clicked cancel so do some stuff */
//							}
//						}).create();
//	}
//
//	private Dialog showFileBodyDialog(LayoutInflater factory) {
//		final View view = factory
//				.inflate(R.layout.alert_dialog_file_info, null);
//		final TextView idView = (TextView) view.findViewById(R.id.id_edit);
//		if (mKiiFile != null)
//			idView.setText(mKiiFile.getString("ID", null));
//		return new AlertDialog.Builder(FileOperationActivity.this)
//				.setIcon(R.drawable.alert_dialog_icon)
//				.setTitle(R.string.file_info)
//				.setView(view)
//				.setPositiveButton(R.string.alert_dialog_ok,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//								if (ConstantValues.mSyncMode) {
//									fileBody(idView.getText().toString());
//								} else {
//									int token = asyncFileBody(idView.getText()
//											.toString());
//									showProcessing(token, "Getting File Body");
//								}
//							}
//						})
//				.setNegativeButton(R.string.alert_dialog_cancel,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//
//								/* User clicked cancel so do some stuff */
//							}
//						}).create();
//	}
//
//	private Dialog showDeleteFileDialog(LayoutInflater factory) {
//		final View view = factory
//				.inflate(R.layout.alert_dialog_file_info, null);
//		final TextView idView = (TextView) view.findViewById(R.id.id_edit);
//		if (mKiiFile != null)
//			idView.setText(mKiiFile.getString("ID", null));
//		return new AlertDialog.Builder(FileOperationActivity.this)
//				.setIcon(R.drawable.alert_dialog_icon)
//				.setTitle(R.string.file_delete)
//				.setView(view)
//				.setPositiveButton(R.string.alert_dialog_ok,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//								if (ConstantValues.mSyncMode) {
//									delete_File(idView.getText().toString());
//								} else {
//									int token = asyncDeleteFile(idView
//											.getText().toString());
//									showProcessing(token, "Deleting file");
//								}
//							}
//						})
//				.setNegativeButton(R.string.alert_dialog_cancel,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//
//								/* User clicked cancel so do some stuff */
//							}
//						}).create();
//	}
//
//	private Dialog showRestoreFileDialog(LayoutInflater factory) {
//		final View view = factory
//				.inflate(R.layout.alert_dialog_file_info, null);
//		final TextView idView = (TextView) view.findViewById(R.id.id_edit);
//		if (mKiiFile != null)
//			idView.setText(mKiiFile.getString("ID", null));
//		return new AlertDialog.Builder(FileOperationActivity.this)
//				.setIcon(R.drawable.alert_dialog_icon)
//				.setTitle(R.string.file_restore)
//				.setView(view)
//				.setPositiveButton(R.string.alert_dialog_ok,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//								if (ConstantValues.mSyncMode) {
//									restore_File(idView.getText().toString());
//								} else {
//									int token = asyncRestoreFile(idView
//											.getText().toString());
//									showProcessing(token, "Restoring file");
//								}
//							}
//						})
//				.setNegativeButton(R.string.alert_dialog_cancel,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//
//								/* User clicked cancel so do some stuff */
//							}
//						}).create();
//	}
//
//	private Dialog showFileSearchDialog(LayoutInflater factory) {
//		final View view = factory
//				.inflate(R.layout.alert_dialog_file_info, null);
//		final TextView textView = (TextView) view.findViewById(R.id.id_edit);
//		final TextView titleView = (TextView) view
//				.findViewById(R.id.title_view);
//		titleView.setText(getString(R.string.file_search_title));
//		return new AlertDialog.Builder(FileOperationActivity.this)
//				.setIcon(R.drawable.alert_dialog_icon)
//				.setTitle(R.string.file_search)
//				.setView(view)
//				.setPositiveButton(R.string.alert_dialog_ok,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//								if (ConstantValues.mSyncMode) {
//									fileSearch(textView.getText().toString());
//								} else {
//									int token = asyncFileSearch(textView
//											.getText().toString());
//									showProcessing(token,
//											"Searching for file...");
//								}
//							}
//						})
//				.setNegativeButton(R.string.alert_dialog_cancel,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//
//								/* User clicked cancel so do some stuff */
//							}
//						}).create();
//	}
//
//	private void showProcessing(final int token, String process_title) {
//		closeProgressDialog();
//
//		LayoutInflater factory = LayoutInflater.from(this);
//
//		final View processView = factory.inflate(R.layout.processing, null);
//		TextView processTitle = (TextView) processView
//				.findViewById(R.id.process_text);
//		processTitle.setText(process_title);
//
//		mProgressDialog = new AlertDialog.Builder(this)
//				.setIcon(R.drawable.alert_dialog_icon)
//				.setTitle("Progressing")
//				.setView(processView)
//				.setPositiveButton(R.string.alert_dialog_background,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//
//							}
//						})
//				.setNegativeButton(R.string.alert_dialog_cancel_task,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//
//								KiiAsyncTaskMananger.cancelTask(token);
//								Toast.makeText(FileOperationActivity.this,
//										"Task has been canceled",
//										Toast.LENGTH_SHORT).show();
//							}
//						}).create();
//
//		mProgressDialog.show();
//	}
//
//	private void closeProgressDialog() {
//		if (mProgressDialog != null && mProgressDialog.isShowing()) {
//			mProgressDialog.dismiss();
//		}
//	}
//
//	private void showException(Exception exception) {
//		String title = "Exception";
//		String msg = (exception != null) ? exception.getMessage() : "NULL";
//		AlertDialog dialog = new AlertDialog.Builder(this)
//				.setIcon(R.drawable.alert_dialog_icon)
//				.setTitle(title)
//				.setMessage(msg)
//				.setPositiveButton(R.string.alert_dialog_ok,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//
//								/* User clicked OK so do some stuff */
//							}
//						}).create();
//
//		dialog.show();
//	}
//
//	private void showSuccess(String msg) {
//
//		String title = "Success";
//		AlertDialog dialog = new AlertDialog.Builder(this)
//				.setIcon(R.drawable.alert_dialog_icon)
//				.setTitle(title)
//				.setMessage(msg)
//				.setPositiveButton(R.string.alert_dialog_ok,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//
//								/* User clicked OK so do some stuff */
//							}
//						}).create();
//
//		dialog.show();
//
//	}
//
//	private void showError(String msg) {
//		String title = "Error";
//		AlertDialog dialog = new AlertDialog.Builder(this)
//				.setIcon(R.drawable.alert_dialog_icon)
//				.setTitle(title)
//				.setMessage(msg)
//				.setPositiveButton(R.string.alert_dialog_ok,
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int whichButton) {
//
//								/* User clicked OK so do some stuff */
//							}
//						}).create();
//
//		dialog.show();
//
//	}
//
//	private void showResult(FileResult result) {
//
//		StringBuilder show = new StringBuilder();
//		show.append("Status: " + result.getStatus());
//		show.append("\n");
//		if (!Utils.isEmpty(result.getKiiFiles())) {
//			show.append("Items:" + result.getKiiFiles().size());
//		}
//		show.append("\nDetails: " + result.getBody());
//		showSuccess(show.toString());
//	}
//
//	private void showFileResult(FileResult result) {
//
//		KiiFile result_file = result.getKiiFiles().get(0);
//		mKiiFile = result_file;
//		StringBuilder show = new StringBuilder();
//		show.append("Status: " + result.getStatus());
//		if (result_file != null)
//			show.append("\nUUID=" + result_file.getString("ID", null));
//		show.append("\n");
//		show.append("Details: " + result.getBody());
//		showSuccess(show.toString());
//
//	}
//
//	private void uploadFile(String file) {
//		try {
//			FileResult result = mClient.getKiiFileManager().upload(file);
//			showFileResult(result);
//		} catch (Exception e) {
//			showException(e);
//		}
//
//	}
//
//	private int asyncUploadFile(final String file) {
//		int token = mClient.getKiiFileManager().upload(
//				new KiiFileTaskCallBack() {
//
//					@Override
//					public void onTaskCompleted(int token, boolean success) {
//						closeProgressDialog();
//						if (success) {
//							showFileResult(getResult());
//						} else {
//							showException(getException());
//						}
//
//					}
//
//				}, file);
//
//		return token;
//
//	}
//
//	private void updateFile(String title, String optional) {
//		try {
//			mKiiFile.setTitle(title);
//			mKiiFile.setOptional(optional);
//			FileResult result = mClient.getKiiFileManager()
//					.updateInfo(mKiiFile);
//			showFileResult(result);
//		} catch (Exception e) {
//			showException(e);
//		}
//
//	}
//
//	private int asyncUpdateFile(String title, String optional) {
//		try {
//			mKiiFile.setTitle(title);
//			mKiiFile.setOptional(optional);
//		} catch (JSONException e) {
//		}
//		int token = mClient.getKiiFileManager().updateInfo(
//				new KiiFileTaskCallBack() {
//
//					@Override
//					public void onTaskCompleted(int token, boolean success) {
//						closeProgressDialog();
//						if (success) {
//							showFileResult(getResult());
//						} else {
//							showException(getException());
//						}
//
//					}
//
//				}, mKiiFile);
//
//		return token;
//
//	}
//
//	private void fileInfo(String id) {
//
//		FileResult result = null;
//		try {
//			mClient.getKiiFileManager().getInfo(id);
//			showFileResult(result);
//		} catch (Exception e) {
//			showException(e);
//			e.printStackTrace();
//		}
//
//	}
//
//	private int asyncFileInfo(final String file) {
//
//		int token = mClient.getKiiFileManager().getInfo(
//				new KiiFileTaskCallBack() {
//					@Override
//					public void onTaskCompleted(int token, boolean success) {
//						closeProgressDialog();
//						if (success) {
//							showFileResult(getResult());
//						} else {
//							showException(getException());
//						}
//
//					}
//
//				}, file);
//
//		return token;
//
//	}
//
//	private void fileBody(String id) {
//
//		FileResult result = null;
//		try {
//			result = mClient.getKiiFileManager().getContent(
//					id,
//					ConstantValues.TARGET_FILE_NAME
//							+ System.currentTimeMillis() + ".jpg");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		showResult(result);
//	}
//
//	private int asyncFileBody(final String file) {
//
//		int token = mClient.getKiiFileManager().getContent(
//				new KiiFileTaskCallBack() {
//					@Override
//					public void onTaskCompleted(int token, boolean success) {
//						closeProgressDialog();
//						closeProgressDialog();
//						if (success) {
//							showResult(getResult());
//						} else {
//							showException(getException());
//						}
//					}
//
//				},
//				file,
//				ConstantValues.TARGET_FILE_NAME + System.currentTimeMillis()
//						+ ".jpg");
//
//		return token;
//
//	}
//
//	private void delete_File(String id) {
//
//		try {
//			mClient.getKiiFileManager().delete(id);
//			showSuccess("");
//		} catch (Exception e) {
//			showException(e);
//		}
//	}
//
//	private int asyncDeleteFile(final String file) {
//		int token = mClient.getKiiFileManager().delete(
//				new KiiFileTaskCallBack() {
//					@Override
//					public void onTaskCompleted(int token, boolean success) {
//						closeProgressDialog();
//						if (success) {
//							showSuccess("");
//						} else {
//							showException(getException());
//						}
//
//					}
//
//				}, file);
//
//		return token;
//
//	}
//
//	private void restore_File(String id) {
//
//		FileResult result = null;
//		try {
//			result = mClient.getKiiFileManager().restore(id);
//			showFileResult(result);
//		} catch (Exception e) {
//			showException(e);
//			e.printStackTrace();
//		}
//
//	}
//
//	private int asyncRestoreFile(final String file) {
//
//		int token = mClient.getKiiFileManager().restore(
//				new KiiFileTaskCallBack() {
//					@Override
//					public void onTaskCompleted(int token, boolean success) {
//						closeProgressDialog();
//						if (success) {
//							showFileResult(getResult());
//						} else {
//							showException(getException());
//						}
//
//					}
//
//				}, file);
//
//		return token;
//
//	}
//
//	private void emptyTrashcan() {
//
//		try {
//			FileResult result = mClient.getKiiFileManager().emptyTrash();
//			showFileResult(result);
//		} catch (Exception e) {
//			showException(e);
//			e.printStackTrace();
//		}
//	}
//
//	private int asyncEmptyTrashcan() {
//
//		int token = mClient.getKiiFileManager().emptyTrash(
//				new KiiFileTaskCallBack() {
//					@Override
//					public void onTaskCompleted(int token, boolean success) {
//						closeProgressDialog();
//						if (success) {
//							showFileResult(getResult());
//						} else {
//							showException(getException());
//						}
//
//					}
//
//				});
//
//		return token;
//
//	}
//
//	private void showListFiles() {
//		try {
//			FileResult result = mClient.getKiiFileManager().listFiles();
//			showResult(result);
//		} catch (Exception e) {
//			showException(e);
//		}
//	}
//
//	private int asyncListFiles() {
//
//		int token = mClient.getKiiFileManager().listFiles(
//				new KiiFileTaskCallBack() {
//					@Override
//					public void onTaskCompleted(int token, boolean success) {
//						closeProgressDialog();
//						if (success) {
//							showResult(getResult());
//						} else {
//							showException(getException());
//						}
//
//					}
//
//				});
//
//		return token;
//	}
//
//	private void fileSearch(String text) {
//
//		// mClient.getKiiFileManager().setVirtualRoot(ConstantValues.VIRTUAL_ROOT);
//		// FileResult result = null;
//		// try {
//		// mClient.getKiiFileManager().search(ql, filter, segments);
//		// showFileResult(result);
//		// } catch (JSONException e) {
//		// showException(e);
//		// e.printStackTrace();
//		// }
//
//	}
//
//	private int asyncFileSearch(final String text) {
//		return 0;
//	}
//
//	private void showListTrashFiles() {
//		try {
//			FileResult result = mClient.getKiiFileManager().listTrash();
//			showResult(result);
//		} catch (Exception e) {
//			showException(e);
//		}
//
//	}
//
//	private int asyncListTrashFiles() {
//		int token = mClient.getKiiFileManager().listTrash(
//				new KiiFileTaskCallBack() {
//					@Override
//					public void onTaskCompleted(int token, boolean success) {
//						closeProgressDialog();
//						if (success) {
//							showResult(getResult());
//						} else {
//							showException(getException());
//						}
//
//					}
//
//				});
//
//		return token;
//	}

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
        
    }
}

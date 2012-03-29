package com.kii.demo.cloudstorage.notepad;

import org.json.JSONException;
import com.kii.demo.cloudstorage.R;
import com.kii.demo.cloudstorage.activities.ConstantValues;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NoteEditor extends Activity {
    private static final String TAG = "NoteEditor";

    private static final int STATE_EDIT = 0;
    private static final int STATE_INSERT = 1;

    private int mState;

    private EditText mText;

    private static final String ORIGINAL_CONTENT = "origContent";
    private String mOriginalContent;

    /**
     * A custom EditText that draws lines between each line of text that is
     * displayed.
     */
    public static class LinedEditText extends EditText {
        private Rect mRect;
        private Paint mPaint;

        // we need this constructor for LayoutInflater
        public LinedEditText(Context context, AttributeSet attrs) {
            super(context, attrs);

            mRect = new Rect();
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(0x800000FF);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int count = getLineCount();
            Rect r = mRect;
            Paint paint = mPaint;

            for (int i = 0; i < count; i++) {
                int baseline = getLineBounds(i, r);

                canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1,
                        paint);
            }

            super.onDraw(canvas);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();

        // Do some setup based on the action being performed.
        final String action = intent.getAction();
        if (Intent.ACTION_EDIT.equals(action)) {
            mState = STATE_EDIT;
            // mUri = intent.getData();
        } else if (Intent.ACTION_INSERT.equals(action)) {
            mState = STATE_INSERT;

        } else {
            // Whoops, unknown action! Bail.
            Log.e(TAG, "Unknown action, exiting");
            finish();
            return;
        }

        // Set the layout for this activity. You can find it in
        // res/layout/note_editor.xml
        setContentView(R.layout.note_editor);

        // The text view for our note, identified by its ID in the XML file.
        mText = (EditText) findViewById(R.id.note);

        // If an instance of this activity had previously stopped, we can
        // get the original text it started with.
        if (savedInstanceState != null) {
            mOriginalContent = savedInstanceState.getString(ORIGINAL_CONTENT);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If we didn't have any trouble retrieving the data, it is now
        // time to get at the stuff.
        String note = "";

        // Modify our overall title depending on the mode we are running in.
        if (mState == STATE_EDIT) {

            String title = getIntent().getStringExtra("title");
            Resources res = getResources();
            String text = String.format(res.getString(R.string.title_edit),
                    title);
            setTitle(text);
            note = getIntent().getStringExtra("body");
        } else if (mState == STATE_INSERT) {
            setTitle(getText(R.string.title_create));
        }

        // This is a little tricky: we may be resumed after previously being
        // paused/stopped. We want to put the new text in the text view,
        // but leave the user where they were (retain the cursor position
        // etc). This version of setText does that for us.

        mText.setTextKeepState(note);

        // If we hadn't previously retrieved the original text, do so
        // now. This allows the user to revert their changes.
        if (mOriginalContent == null) {
            mOriginalContent = note;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.
        outState.putString(ORIGINAL_CONTENT, mOriginalContent);
    }

    private boolean isSave = true;

    @Override
    public void finish() {
        if (isSave) {
            saveNote();
        }
        super.finish();
    }

    public void handleSave(View v) {
        isSave = true;
        finish();
    }

    public void handleCancel(View v) {
        isSave = false;
        finish();
    }

    private final void saveNote() {
        String text = mText.getText().toString();
        int length = text.length();

        if (mState == STATE_INSERT) {
            if (length == 0) {
                Toast.makeText(this, R.string.nothing_to_save,
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String title = text.substring(0, Math.min(30, length));
            if (length > 30) {
                int lastSpace = title.lastIndexOf(' ');
                if (lastSpace > 0) {
                    title = title.substring(0, lastSpace);
                }
            }
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_INSERT);
            intent.putExtra("title", title);
            intent.putExtra("body", text);
            setResult(RESULT_OK, intent);
        } else if (mState == STATE_EDIT) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_EDIT);
            intent.putExtra("body", text);
            setResult(RESULT_OK, intent);
        }

    }

}

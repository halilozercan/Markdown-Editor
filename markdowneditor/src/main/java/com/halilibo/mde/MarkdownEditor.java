package com.halilibo.mde;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.halilibo.mde.actions.ActionAdapter;
import com.halilibo.mde.actions.ImageAction;
import com.halilibo.mde.actions.MarkdownAction;
import com.halilibo.mde.actions.TextAction;
import com.halilibo.mde.styles.MarkdownStyle;
import com.halilibo.mde.styles.StyleDialogFragment;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

import us.feras.mdv.MarkdownView;

public class MarkdownEditor extends FrameLayout implements UserMethods{

    @IntDef({EDITOR_ACTION, PREVIEW_ACTION})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActionType {

    }
    public static final int EDITOR_ACTION = 0;
    public static final int PREVIEW_ACTION = 1;

    public static final String ACTION_PASTE = "com.halilibo.mdet.MarkdownEditText.ACTION_PASTE";
    public static final String ACTION_HEADER = "com.halilibo.mdet.MarkdownEditText.ACTION_HEADER";
    public static final String ACTION_BOLD = "com.halilibo.mdet.MarkdownEditText.ACTION_BOLD";
    public static final String ACTION_ITALIC = "com.halilibo.mdet.MarkdownEditText.ACTION_ITALIC";
    public static final String ACTION_QUOTE = "com.halilibo.mdet.MarkdownEditText.ACTION_QUOTE";
    public static final String ACTION_CODE = "com.halilibo.mdet.MarkdownEditText.ACTION_CODE";
    public static final String ACTION_IMAGE = "com.halilibo.mdet.MarkdownEditText.ACTION_IMAGE";
    public static final String ACTION_STYLE = "com.halilibo.mdet.MarkdownEditText.ACTION_STYLE";

    public static final String STYLE_DARK = "Dark";
    public static final String STYLE_GITHUB = "Github";
    public static final String STYLE_PAPERWHITE = "Paperwhite";

    private EditText mEditText;
    private ImageButton mPreviewButton;
    private ArrayList<MarkdownAction> mEditorActions = new ArrayList<>();
    private ArrayList<MarkdownAction> mPreviewActions = new ArrayList<>();
    private ArrayList<MarkdownStyle> mStyles = new ArrayList<>();

    private int mStyleNumber;

    private int mThemeColor;
    private View mViewsFrame;
    private MarkdownView markDownView;
    private boolean mPreviewToggle;
    private MarkdownCallback callback = null;
    private int mControlsGravity;

    private RecyclerView editorActionsRecyclerView;
    private RecyclerView previewActionsRecyclerView;

    public MarkdownEditor(Context context) {
        super(context);
        init(context, null);
    }

    public MarkdownEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MarkdownEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public EditText getEditText(){
        return mEditText;
    }

    @Override
    public int addStyle(String name, String cssFile, Drawable preview) {
        return 0;
    }

    public void setCallback(MarkdownCallback callback) {
        this.callback = callback;
    }

    public boolean addStyle(MarkdownStyle style){
        if(!mStyles.contains(style)){
            mStyles.add(style);
            return true;
        }
        return false;
    }

    public boolean removeStyle(String styleName){
        for(int i=0;i<mStyles.size();i++)
            if(mStyles.get(i).getName().equals(styleName)) {
                if(i == mStyleNumber)
                    throw new IllegalArgumentException("You can't remove a style that is already selected. Select another one and then try again!");
                else {
                    mStyles.remove(i);
                    return true;
                }
            }
        return false;
    }

    public boolean addAction(MarkdownAction action, @ActionType int type){
        if(!action.isValid())
            return false;

        if(type == EDITOR_ACTION && !mEditorActions.contains(action)) {
            mEditorActions.add(action);
        }
        else if(type == PREVIEW_ACTION && !mPreviewActions.contains(action)){
            mPreviewActions.add(action);
        }
        invalidateActions();
        return true;
    }

    public void removeAction(String actionName, @ActionType int type){
        if(type == EDITOR_ACTION) {
            for(int i=0;i<mEditorActions.size();i++)
                if(mEditorActions.get(i).getName().equals(actionName)) {
                    mEditorActions.remove(i);
                    break;
                }
        }
        else if(type == PREVIEW_ACTION){
            for(int i=0;i<mPreviewActions.size();i++)
                if(mPreviewActions.get(i).getName().equals(actionName)) {
                    mPreviewActions.remove(i);
                    break;
                }
        }
        invalidateActions();
    }

    public void showPreview(){
        markDownView.loadMarkdown(mEditText.getText().toString(),
                mStyles.get(mStyleNumber).getAddress());

        markDownView.setVisibility(VISIBLE);
        mEditText.setVisibility(GONE);
        previewActionsRecyclerView.setVisibility(VISIBLE);
        editorActionsRecyclerView.setVisibility(GONE);

        mPreviewButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.edit));
        mPreviewToggle = true;
        invalidateActions();
    }

    public void showEditor(){
        markDownView.setVisibility(GONE);
        mEditText.setVisibility(VISIBLE);
        previewActionsRecyclerView.setVisibility(GONE);
        editorActionsRecyclerView.setVisibility(VISIBLE);
        mPreviewButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.preview));

        mPreviewToggle = false;
        invalidateActions();
    }

    private void init(Context context, AttributeSet attrs) {

        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.MarkdownEditor,
                    0, 0);
            try {

                mThemeColor = a.getColor(R.styleable.MarkdownEditor_markThemeColor,
                        Util.resolveColor(context, R.attr.colorPrimary));

                mControlsGravity = a.getInteger(R.styleable.MarkdownEditor_markControlsGravity, 0);
                mStyleNumber = a.getInt(R.styleable.MarkdownEditor_markStyle, 2);

            } finally {
                a.recycle();
            }
        } else {
            mThemeColor = Util.resolveColor(context, R.attr.colorPrimary);
            mControlsGravity = 0;
            mStyleNumber = 2;
        }

        mStyles.add(new MarkdownStyle(STYLE_DARK, "file:///android_asset/dark.css",
                ContextCompat.getDrawable(context, R.drawable.style_dark)));
        mStyles.add(new MarkdownStyle(STYLE_GITHUB, "file:///android_asset/github.css",
                ContextCompat.getDrawable(context, R.drawable.style_github)));
        mStyles.add(new MarkdownStyle(STYLE_PAPERWHITE, "file:///android_asset/paperwhite.css",
                ContextCompat.getDrawable(context, R.drawable.style_paperwhite)));

        applyThemeColor(context);

        mPreviewToggle = false;
    }

    private void applyThemeColor(Context context) {
        LayerDrawable drawable = (LayerDrawable)
                ContextCompat.getDrawable(context, R.drawable.edittext_background);
        GradientDrawable gradientDrawable = (GradientDrawable)
                drawable.findDrawableByLayerId(R.id.edittext_background_item);
        gradientDrawable.setColor(mThemeColor); // change color

        drawable = (LayerDrawable) ContextCompat.getDrawable(context,
                R.drawable.controls_background);
        gradientDrawable = (GradientDrawable)
                drawable.findDrawableByLayerId(R.id.controls_background_item);
        gradientDrawable.setColor(mThemeColor); // change color

        gradientDrawable = (GradientDrawable)
                drawable.findDrawableByLayerId(R.id.controls_background_item_2);
        gradientDrawable.setColor(mThemeColor); // change color
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // Instantiate and add TextureView for rendering
        final LayoutInflater li = LayoutInflater.from(getContext());

        switch (mControlsGravity){
            case 0:
                mViewsFrame = li.inflate(R.layout.views_top, this, false);
                break;
            case 1:
                mViewsFrame = li.inflate(R.layout.views_bottom, this, false);
                break;
            case 2:
                mViewsFrame = li.inflate(R.layout.views_left, this, false);
                break;
            case 3:
                mViewsFrame = li.inflate(R.layout.views_right, this, false);
                break;
        }
        LayoutParams viewsLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mViewsFrame, viewsLayoutParams);

        mEditText = (EditText) mViewsFrame.findViewById(R.id.editText);
        markDownView = (MarkdownView) mViewsFrame.findViewById(R.id.markdownView);

        editorActionsRecyclerView = (RecyclerView) findViewById(R.id.editor_actions_recyclerview);
        previewActionsRecyclerView = (RecyclerView) findViewById(R.id.preview_actions_recyclerview);

        editorActionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        previewActionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));

        mPreviewButton = (ImageButton) mViewsFrame.findViewById(R.id.preview_button);
        mPreviewButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mPreviewToggle){
                    showPreview();
                }
                else{
                    showEditor();
                }
                if(callback!=null){
                    callback.previewToggled(mPreviewToggle);
                }
            }
        });

        ImageAction pasteAction = new ImageAction();
        pasteAction.setName(ACTION_PASTE);
        pasteAction.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.paste));
        pasteAction.setCallback(new MarkdownAction.MarkdownActionCallback() {
            @Override
            public void onActivate(MarkdownAction action) {
                String clipboardText = readFromClipboard();
                if(clipboardText!=null){
                    int start = Math.max(mEditText.getSelectionStart(), 0);

                    mEditText.getText().insert(start, clipboardText);
                    mEditText.setSelection(start+clipboardText.length());
                }
            }
        });
        mEditorActions.add(pasteAction);

        ImageAction headerAction = new ImageAction();
        headerAction.setName(ACTION_HEADER);
        headerAction.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.header));
        headerAction.setCallback(new MarkdownAction.MarkdownActionCallback() {
            @Override
            public void onActivate(MarkdownAction action) {
                FragmentManager fm = ((Activity)getContext()).getFragmentManager();
                HeaderDialogFragment
                        .newInstance(new HeaderDialogFragment.HeaderDialogCallback() {
                            @Override
                            public void onHeader(int level) {
                                String headerPrefix = "\n";
                                for(int i=0;i<level;i++)headerPrefix+="#";

                                int start = Math.max(mEditText.getSelectionStart(), 0);

                                mEditText.getText().insert(start, headerPrefix + " ");
                                mEditText.setSelection(start+level+2);
                            }
                        })
                        .show(fm, "fragment_header_dialog");
            }
        });
        mEditorActions.add(headerAction);

        ImageAction boldAction = new ImageAction();
        boldAction.setName(ACTION_BOLD);
        boldAction.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bold));
        boldAction.setCallback(new MarkdownAction.MarkdownActionCallback() {
            @Override
            public void onActivate(MarkdownAction action) {
                int start = Math.max(mEditText.getSelectionStart(), 0);
                int end = Math.max(mEditText.getSelectionEnd(), 0);

                mEditText.getText().insert(Math.max(start, end), "**");
                mEditText.getText().insert(Math.min(start, end), "**");
                mEditText.setSelection(start+2, end+2);
            }
        });
        mEditorActions.add(boldAction);

        ImageAction italicAction = new ImageAction();
        italicAction.setName(ACTION_ITALIC);
        italicAction.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.italic));
        italicAction.setCallback(new MarkdownAction.MarkdownActionCallback() {
            @Override
            public void onActivate(MarkdownAction action) {
                int start = Math.max(mEditText.getSelectionStart(), 0);
                int end = Math.max(mEditText.getSelectionEnd(), 0);

                mEditText.getText().insert(Math.max(start, end), "_");
                mEditText.getText().insert(Math.min(start, end), "_");
                mEditText.setSelection(start+1, end+1);
            }
        });
        mEditorActions.add(italicAction);

        ImageAction quoteAction = new ImageAction();
        quoteAction.setName(ACTION_QUOTE);
        quoteAction.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.quote));
        quoteAction.setCallback(new MarkdownAction.MarkdownActionCallback() {
            @Override
            public void onActivate(MarkdownAction action) {
                int start = Math.max(mEditText.getSelectionStart(), 0);

                mEditText.getText().insert(start, "\n\n> ");
                mEditText.setSelection(start+4);
            }
        });
        mEditorActions.add(quoteAction);

        ImageAction codeAction = new ImageAction();
        codeAction.setName(ACTION_CODE);
        codeAction.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.code));
        codeAction.setCallback(new MarkdownAction.MarkdownActionCallback() {
            @Override
            public void onActivate(MarkdownAction action) {
                int start = Math.max(mEditText.getSelectionStart(), 0);
                int end = Math.max(mEditText.getSelectionEnd(), 0);

                mEditText.getText().insert(Math.max(start, end), "```");
                mEditText.getText().insert(Math.min(start, end), "```");
                mEditText.setSelection(start+3, end+3);
            }
        });
        mEditorActions.add(codeAction);

        ImageAction imageAction = new ImageAction();
        imageAction.setName(ACTION_IMAGE);
        imageAction.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.image));
        imageAction.setCallback(new MarkdownAction.MarkdownActionCallback() {
            @Override
            public void onActivate(MarkdownAction action) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.mark_enter_url_title)
                        .input(getContext().getString(R.string.mark_url_hint),
                            "",
                            new MaterialDialog.InputCallback() {
                                @Override
                                public void onInput(@NonNull MaterialDialog dialog,
                                                    CharSequence input) {
                                    int start = Math.max(mEditText.getSelectionStart(), 0);
                                    int end = Math.max(mEditText.getSelectionEnd(), 0);

                                    mEditText.getText().insert(
                                            Math.min(start,end),
                                            "\n![](" + input.toString() + ")\n");

                                }
                            }).show();
            }
        });
        mEditorActions.add(imageAction);

        TextAction styleAction = new TextAction();
        styleAction.setName(ACTION_STYLE);
        styleAction.setText(String.format(getResources().getString(R.string.style_button_format),
                mStyles.get(mStyleNumber).getName()));
        styleAction.setCallback(new MarkdownAction.MarkdownActionCallback() {
            @Override
            public void onActivate(final MarkdownAction action) {
                FragmentManager fm = ((Activity)getContext()).getFragmentManager();
                StyleDialogFragment
                        .newInstance(new StyleDialogFragment.StyleDialogCallback() {
                            @Override
                            public void onStyle(int choice) {
                                mStyleNumber = choice;
                                ((TextAction)action).setText(
                                        String.format(getResources()
                                            .getString(R.string.style_button_format),
                                        mStyles.get(mStyleNumber).getName()));
                                invalidateActions();
                                showPreview();
                            }
                        }, mStyles)
                        .show(fm, "fragment_style_dialog");
            }
        });
        mPreviewActions.add(styleAction);

        invalidateActions();
    }

    private void invalidateActions(){
        ActionAdapter editorActionsAdapter = new ActionAdapter(getContext());
        editorActionsAdapter.setThemeColor(mThemeColor);
        editorActionsAdapter.setData(mEditorActions);
        editorActionsRecyclerView.setAdapter(editorActionsAdapter);

        ActionAdapter previewActionsAdapter = new ActionAdapter(getContext());
        previewActionsAdapter.setThemeColor(mThemeColor);
        previewActionsAdapter.setData(mPreviewActions);
        previewActionsRecyclerView.setAdapter(previewActionsAdapter);
    }

    private String readFromClipboard() {
        ClipboardManager clipboard =
                (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard.hasPrimaryClip()) {
            android.content.ClipDescription description = clipboard.getPrimaryClipDescription();
            android.content.ClipData data = clipboard.getPrimaryClip();
            if (data != null && description != null
                    && description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))
                return String.valueOf(data.getItemAt(0).getText());
        }
        return null;
    }

    public Bitmap getBitmap() {
        markDownView.measure(MeasureSpec.makeMeasureSpec(
                MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        Bitmap bmp = Bitmap.createBitmap(markDownView.getMeasuredWidth(),
                markDownView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas bigcanvas = new Canvas(bmp);
        Paint paint = new Paint();
        int iHeight = bmp.getHeight();
        bigcanvas.drawBitmap(bmp, 0, iHeight, paint);
        markDownView.draw(bigcanvas);

        return bmp;
    }
}

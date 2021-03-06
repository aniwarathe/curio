package com.curiolearning.curio.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.curiolearning.curio.R;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;

/**
 * Created by Chinthaka on 12/17/2017.
 */

public class ChaptersViewHolder extends AbstractExpandableItemViewHolder {
    public TextView chapterTitle;
    public ImageView chapterBackground;
    public ChaptersViewHolder(View itemView) {
        super(itemView);
        chapterTitle=itemView.findViewById(R.id.chapter_title);
        chapterBackground=itemView.findViewById(R.id.image_color);
    }
}

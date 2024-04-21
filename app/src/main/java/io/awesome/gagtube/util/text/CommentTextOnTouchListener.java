package io.awesome.gagtube.util.text;

import android.annotation.SuppressLint;
import android.text.Layout;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CommentTextOnTouchListener implements View.OnTouchListener {

    public static final CommentTextOnTouchListener INSTANCE = new CommentTextOnTouchListener();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (!(v instanceof TextView)) {
            return false;
        }
        final TextView widget = (TextView) v;
        final CharSequence text = widget.getText();
        if (text instanceof Spanned) {
            final Spanned buffer = (Spanned) text;
            final int action = event.getAction();

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                final int offset = getOffsetForHorizontalLine(widget, event);
                final ClickableSpan[] links = buffer.getSpans(offset, offset, ClickableSpan.class);

                if (links.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        links[0].onClick(widget);
                    }
                    // we handle events that intersect links, so return true
                    return true;
                }
            }
        }
        return false;
    }

    public static int getOffsetForHorizontalLine(@NonNull final TextView textView,
                                                 @NonNull final MotionEvent event) {

        int x = (int) event.getX();
        int y = (int) event.getY();

        x -= textView.getTotalPaddingLeft();
        y -= textView.getTotalPaddingTop();

        x += textView.getScrollX();
        y += textView.getScrollY();

        final Layout layout = textView.getLayout();
        final int line = layout.getLineForVertical(y);
        return layout.getOffsetForHorizontal(line, x);
    }
}

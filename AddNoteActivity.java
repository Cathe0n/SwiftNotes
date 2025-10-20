package org.geeksforgeeks.simple_notes_application_java;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

/**
 * AddNoteActivity with a built-in color wheel dialog (no external libs).
 * Returns extras:
 *  - "NEW_NOTE" (title)
 *  - "NEW_TAG"
 *  - "NEW_CONTENT"
 *  - "NEW_COLOR" (int, Android color)
 */
public class AddNoteActivity extends AppCompatActivity {

    private int selectedColor = Color.parseColor("#00C6FF"); // default

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note); // keep your xml name

        final EditText etTitle = findViewById(R.id.etTitle);
        final EditText etTag = findViewById(R.id.etTag);
        final EditText etContent = findViewById(R.id.etContent);
        final View vPreview = findViewById(R.id.vColorPreview);
        Button btnCreate = findViewById(R.id.btnCreate);

        // If your layout has an "open color wheel" button, use its id.
        // If not present, you can open the wheel by tapping the preview.
        final View btnOpen = findViewById(
                getResources().getIdentifier("btnOpenColorWheel", "id", getPackageName())
        );

        // set initial preview color
        if (vPreview != null) vPreview.setBackgroundColor(selectedColor);

        // open wheel on preview tap as fallback if there's no explicit button
        if (btnOpen != null) {
            btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showColorWheelDialog(vPreview);
                }
            });
        } else if (vPreview != null) {
            vPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showColorWheelDialog(vPreview);
                }
            });
        }

        // Create button handler
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = etTitle.getText().toString().trim();
                String tag = etTag.getText().toString().trim();
                String content = etContent.getText().toString().trim();

                if (title.isEmpty()) {
                    etTitle.setError("Title required");
                    return;
                }

                Intent data = new Intent();
                data.putExtra("NEW_NOTE", title);
                data.putExtra("NEW_TAG", tag);
                data.putExtra("NEW_CONTENT", content);
                data.putExtra("NEW_COLOR", selectedColor);

                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    /**
     * Shows a dialog with a touchable color wheel. When user taps wheel the selected color is
     * previewed and committed with OK. The preview view (vPreview) is updated live.
     */
    private void showColorWheelDialog(@NonNull final View vPreview) {
        final int wheelSize = Math.round(getResources().getDisplayMetrics().density * 280); // dp -> px approx

        // Generate wheel bitmap
        final Bitmap wheelBmp = createColorWheelBitmap(wheelSize);

        // Create dialog views
        ImageView wheelView = new ImageView(this);
        wheelView.setImageBitmap(wheelBmp);
        wheelView.setAdjustViewBounds(true);

        final View smallPreview = new View(this);
        int pvSize = Math.round(getResources().getDisplayMetrics().density * 36);
        LinearLayout.LayoutParams pvLp = new LinearLayout.LayoutParams(pvSize, pvSize);
        pvLp.gravity = Gravity.CENTER_VERTICAL;
        smallPreview.setLayoutParams(pvLp);
        smallPreview.setBackgroundColor(selectedColor);

        TextView hexTv = new TextView(this);
        hexTv.setText(colorToHex(selectedColor));
        hexTv.setPadding(16, 0, 0, 0);
        hexTv.setTextColor(Color.WHITE);

        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);
        topRow.setPadding(8, 8, 8, 8);
        topRow.addView(smallPreview);
        topRow.addView(hexTv);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(topRow);
        FrameLayout wheelFrame = new FrameLayout(this);
        wheelFrame.addView(wheelView);
        container.addView(wheelFrame);

        // Touch listener: sample bitmap at touch point
        wheelView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // convert view coords to bitmap coords
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    float vx = event.getX();
                    float vy = event.getY();

                    float scaleX = (float) wheelBmp.getWidth() / v.getWidth();
                    float scaleY = (float) wheelBmp.getHeight() / v.getHeight();
                    int bmpX = Math.round(vx * scaleX);
                    int bmpY = Math.round(vy * scaleY);

                    if (bmpX >= 0 && bmpY >= 0 && bmpX < wheelBmp.getWidth() && bmpY < wheelBmp.getHeight()) {
                        int pixel = wheelBmp.getPixel(bmpX, bmpY);
                        int alpha = Color.alpha(pixel);
                        // ignore fully transparent pixels (outside wheel)
                        if (alpha == 0) return true;
                        selectedColor = pixel | 0xFF000000; // ensure opaque
                        smallPreview.setBackgroundColor(selectedColor);
                        hexTv.setText(colorToHex(selectedColor));
                    }
                }
                return true;
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Pick color")
                .setView(container)
                .setPositiveButton("OK", (d, which) -> {
                    vPreview.setBackgroundColor(selectedColor);
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();
    }

    private static String colorToHex(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    /**
     * Create a circular HSV color wheel bitmap.
     * center -> full saturation/value gradient, angle->hue
     */
    private static Bitmap createColorWheelBitmap(int sizePx) {
        final int size = Math.max(16, sizePx);
        Bitmap bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        int cx = size / 2;
        int cy = size / 2;
        float radius = size * 0.5f;

        int[] pixels = new int[size * size];

        float[] hsv = new float[]{0f, 0f, 1f};

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = x - cx;
                float dy = y - cy;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist <= radius) {
                    // angle for hue (0-360)
                    double angle = Math.atan2(dy, dx);
                    float hue = (float) Math.toDegrees(angle);
                    if (hue < 0) hue += 360f;
                    // saturation based on distance (0 center -> 1 edge)
                    float sat = (float) (dist / radius);
                    hsv[0] = hue;
                    hsv[1] = sat;
                    hsv[2] = 1f; // brightness fixed at 1
                    int color = Color.HSVToColor(hsv);
                    pixels[y * size + x] = color;
                } else {
                    // outside wheel -> transparent
                    pixels[y * size + x] = 0x00000000;
                }
            }
        }

        bmp.setPixels(pixels, 0, size, 0, 0, size, size);

        // overlay a radial gradient to improve visibility (optional)
        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        int[] gradColors = new int[]{0x00FFFFFF, 0x33000000};
        float[] stops = new float[]{0.7f, 1f};
        RadialGradient rg = new RadialGradient(cx, cy, radius, gradColors, stops, Shader.TileMode.CLAMP);
        paint.setShader(rg);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(cx, cy, radius, paint);

        return bmp;
    }
}

package com.wut.identity_data_provider.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.wut.identity_data_provider.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Class responsible for managing the grid of points that user selects to perform the pattern tests.
 */
@RequiresApi(api = Build.VERSION_CODES.O)
public class ViewGridAdapter extends BaseAdapter {

    final LayoutInflater inflater;
    final int sWidth;

    final RadioButton[] rbs = new RadioButton[9];
    final TextView[] textId = new TextView[9];

    final ArrayList<Integer> passwd = new ArrayList<>(); //correct
    final ArrayList<Integer> inPasswd = new ArrayList<>(); //input


    /**
     * Grid adapter constructor.
     *
     * @param applicationContext Context of the application.
     * @param pattern Currently selected pattern - in a String format.
     * @param width  Calculated width of a single cell in a grid.
     */
    public ViewGridAdapter(Context applicationContext, int width, String pattern) {
        sWidth = width;
        inflater = (LayoutInflater.from(applicationContext));

        passwd.addAll(parsePattern(pattern));
    }


    /**
     * Mandatory method - grid is always 3x3.
     */
    @Override
    public int getCount() {
        return 9;
    }

    /**
     * Mandatory method - returns a radiobutton located in a grid under selected position.
     */
    @Override
    public Object getItem(int position) {
        return rbs[position];
    }

    /**
     * Mandatory method - not used, always returns 0.
     */
    @Override
    public long getItemId(int i) {
        return 0;
    }


    /**
     * Method used for parsing pattern saved in String format to an array of ID integers.
     *
     * @param pattern String describing a selected pattern.
     *
     * @return Returns array of integers of radiobutton IDs making a pattern.
     */
    public ArrayList<Integer> parsePattern(String pattern) {
        ArrayList<Integer> password = new ArrayList<>();
        String[] splitPattern = pattern.split("-");
        if (splitPattern.length > 1) for (String s : splitPattern) password.add(Integer.valueOf(s));
        return password;
    }

    /**
     * Method for getting a JSON object for button positions.
     *
     * @return Returns a JSON object with button border positions.
     */
    public JSONObject calibrationSetting() throws JSONException {
        JSONObject calibration = new JSONObject();
        for (int i = 0; i < rbs.length; i++) {
            Rect rect = new Rect();
            rbs[i].getGlobalVisibleRect(rect);
            JSONObject button = new JSONObject();
            button.put("left", rect.left);
            button.put("right", rect.right);
            button.put("top", rect.top);
            button.put("bottom", rect.bottom);
            calibration.put(String.valueOf(i), button);
        }
        return calibration;
    }


    /**
     * Check if the provided pattern is correct.
     *
     * @return Returns true if the drawn pattern was correct and clears it.
     *          Also handles drawing new patterns. New pattern has to be at least 4 points long.
     */
    public boolean verifyResult() {
        //First if is for drawing a new pattern.
        if (passwd.size() == 0) {
            if (inPasswd.size() < 4) {
                getAndClearInPasswd();
                return false;
            }
        } else {
            boolean result = inPasswd.equals(passwd);
            getAndClearInPasswd();
            return result;
        }
        return true;
    }

    /**
     * Copies the currently provided pattern and clears it.
     *
     * @return Returns an array of integers describing the provided pattern.
     */
    public ArrayList<Integer> getAndClearInPasswd() {
        ArrayList<Integer> passwd = new ArrayList<>(inPasswd);
        inPasswd.clear();
        return passwd;
    }


    /**
     * Returns a list of points located on the screen for the drawn pattern.
     *
     * @return Returns an array of points located at drawn pattern points.
     */
    public List<PointF> getSelected(float top) {
        List<PointF> points = new ArrayList<>();
        for (int i : inPasswd) {
            Rect rect = new Rect();
            rbs[i].getGlobalVisibleRect(rect);

            points.add(new PointF(rect.centerX(), rect.centerY() - top));
        }
        return points;
    }

    /**
     * Sets up the grid in correct position/calibration with square sizes.
     *
     * @param pointer Id of the cell to set up.
     * @param view    View of the singular cell within the grid.
     * @param viewGroup Not used.
     *
     * @return Returns a singular cell in the grid.
     */
    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(final int pointer, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.view_grid, null); // inflate the layout

        ConstraintLayout vw = view.findViewById(R.id.gridID);
        vw.setMinHeight(sWidth);
        vw.setMaxHeight(sWidth);

        textId[pointer] = view.findViewById(R.id.radioId);
        int idx = passwd.indexOf(pointer);
        if (idx == -1) textId[pointer].setText("");
        else textId[pointer].setText(String.valueOf(idx + 1));

        rbs[pointer] = view.findViewById(R.id.radioGrid);
        rbs[pointer].setOnClickListener(v -> {
            if (inPasswd.size() > 0) {
                intermediateToggles(pointer);
            }

            inPasswd.add(pointer);
            rbs[pointer].setOnClickListener(null);
        });

        return view;
    }


    /**
     * Method to set up intermediate toggles. Whenever a user draws a pattern and a line goes over a
     * point that has not yet been selected, then that point has to be selected first.
     *
     * @param pointer Id of the cell that has just been selected.
     */
    private void intermediateToggles(int pointer) {
        switch (pointer) {
            case 0:
                if ((inPasswd.get(inPasswd.size() - 1) == 2) && (!inPasswd.contains(1))) {
                    rbs[1].toggle();
                    rbs[1].callOnClick();
                } else if ((inPasswd.get(inPasswd.size() - 1) == 6) && (!inPasswd.contains(3))) {
                    rbs[3].toggle();
                    rbs[3].callOnClick();
                } else if ((inPasswd.get(inPasswd.size() - 1) == 8) && (!inPasswd.contains(4))) {
                    rbs[4].toggle();
                    rbs[4].callOnClick();
                }
                break;
            case 1:
                if ((inPasswd.get(inPasswd.size() - 1) == 7) && (!inPasswd.contains(4))) {
                    rbs[4].toggle();
                    rbs[4].callOnClick();
                }
                break;
            case 2:
                if ((inPasswd.get(inPasswd.size() - 1) == 0) && (!inPasswd.contains(1))) {
                    rbs[1].toggle();
                    rbs[1].callOnClick();
                } else if ((inPasswd.get(inPasswd.size() - 1) == 8) && (!inPasswd.contains(5))) {
                    rbs[5].toggle();
                    rbs[5].callOnClick();
                } else if ((inPasswd.get(inPasswd.size() - 1) == 7) && (!inPasswd.contains(4))) {
                    rbs[4].toggle();
                    rbs[4].callOnClick();
                }
                break;
            case 3:
                if ((inPasswd.get(inPasswd.size() - 1) == 5) && (!inPasswd.contains(4))) {
                    rbs[4].toggle();
                    rbs[4].callOnClick();
                }
                break;
            case 5:
                if ((inPasswd.get(inPasswd.size() - 1) == 3) && (!inPasswd.contains(4))) {
                    rbs[4].toggle();
                    rbs[4].callOnClick();
                }
                break;
            case 6:
                if ((inPasswd.get(inPasswd.size() - 1) == 8) && (!inPasswd.contains(7))) {
                    rbs[7].toggle();
                    rbs[7].callOnClick();
                } else if ((inPasswd.get(inPasswd.size() - 1) == 0) && (!inPasswd.contains(3))) {
                    rbs[3].toggle();
                    rbs[3].callOnClick();
                } else if ((inPasswd.get(inPasswd.size() - 1) == 2) && (!inPasswd.contains(4))) {
                    rbs[4].toggle();
                    rbs[4].callOnClick();
                }
                break;
            case 7:
                if ((inPasswd.get(inPasswd.size() - 1) == 1) && (!inPasswd.contains(4))) {
                    rbs[4].toggle();
                    rbs[4].callOnClick();
                }
                break;
            case 8:
                if ((inPasswd.get(inPasswd.size() - 1) == 6) && (!inPasswd.contains(7))) {
                    rbs[7].toggle();
                    rbs[7].callOnClick();
                } else if ((inPasswd.get(inPasswd.size() - 1) == 2) && (!inPasswd.contains(5))) {
                    rbs[5].toggle();
                    rbs[5].callOnClick();
                } else if ((inPasswd.get(inPasswd.size() - 1) == 0) && (!inPasswd.contains(4))) {
                    rbs[4].toggle();
                    rbs[4].callOnClick();
                }
                break;
            default:
        }
    }
}

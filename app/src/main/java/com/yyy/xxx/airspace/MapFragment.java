package com.yyy.xxx.airspace;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yyy.xxx.airspace.Model.Board;
import com.yyy.xxx.airspace.Model.BoardLab;
import com.yyy.xxx.airspace.search.Item;
import com.yyy.xxx.airspace.search.OnFinishSearchListener;
import com.yyy.xxx.airspace.search.Searcher;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;


public class MapFragment extends Fragment implements MapView.MapViewEventListener, MapView.POIItemEventListener, ACTIVITY_REQUEST{

    private static final String ARG_PARAM1 = null;
    private static final String ARG_PARAM2 = null;

    public static final String API_KEY = "c230fe73a3ea6267acd784b910b96593";
    private static final String TAG = "MapFragment";
    private static final String DIALOG_CONFIRM = "confirm";

    private String mParam1;
    private String mParam2;
    protected MapPoint mMapPoint;

    private HashMap<Integer, Item> mTagItemMap = new HashMap<Integer, Item>();


    @BindView(R.id.map_view)
    MapView mapView;

    //Daum지도 검색.
    @BindView(R.id.button_search_place)
    EditText edit_Search;

    private Button btn_Search;
    private MapPOIItem mDefaultMarker;
    private MapPOIItem mCustomMarker;


    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String point);
    }

    public MapFragment() {

    }


    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    // CalloutBalloonAdapter 인터페이스 구현
    class MyCustomBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public MyCustomBalloonAdapter() {
            mCalloutBalloon = View.inflate(getActivity(), R.layout.custom_callout_balloon, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {

            return null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = (MapView) view.findViewById(R.id.map_view);

        mapView.setTag("map");

        btn_Search = (Button) view.findViewById(R.id.button_search_place);
        edit_Search = (EditText) view.findViewById(R.id.edit_search_place);

        mapView.setDaumMapApiKey(API_KEY);
        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
//        mapView.setCalloutBalloonAdapter(new MyCustomBalloonAdapter2());


        btn_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = edit_Search.getText().toString();
                if (query == null || query.length() == 0) {
                    Toast.makeText(getContext(), "검색어를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                hideSoftKeyboard(); // 키보드 숨김

                MapPoint.GeoCoordinate geoCoordinate = mapView.getMapCenterPoint().getMapPointGeoCoord();

                double latitude = geoCoordinate.latitude; // 위도
                double longitude = geoCoordinate.longitude; // 경도
                int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
                int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
                String apikey = API_KEY;

                Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
                searcher.searchKeyword( getContext(), query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
                    @Override
                    public void onSuccess(List<Item> itemList) {

                        mapView.removeAllPOIItems(); // 기존 검색 결과 삭제
                        showResult(itemList); // 검색 결과 보여줌
                    }

                    @Override
                    public void onFail() {
                        showToast("API_KEY의 제한 트래픽이 초과되었습니다.");
                    }
                });

            }
        });
        return view;
    }

    private void showToast(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 지도에서 검색했을 때 나오는 부분들을 마커로 찍는 함수
     * @param itemList
     */
    private void showResult(List<Item> itemList) {
            MapPointBounds mapPointBounds = new MapPointBounds();

            for (int i = 0; i < itemList.size(); i++) {
                Item item = itemList.get(i);

                MapPOIItem poiItem = new MapPOIItem();
                poiItem.setItemName(item.title);
                poiItem.setTag(i);
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(item.latitude, item.longitude);
                poiItem.setMapPoint(mapPoint);
                mapPointBounds.add(mapPoint);
                poiItem.setMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomImageResourceId(R.drawable.map_pin_blue);
                poiItem.setSelectedMarkerType(MapPOIItem.MarkerType.CustomImage);
                poiItem.setCustomSelectedImageResourceId(R.drawable.map_pin_red);
                poiItem.setCustomImageAutoscale(false);
                poiItem.setCustomImageAnchor(0.5f, 1.0f);

                mapView.addPOIItem(poiItem);
                mTagItemMap.put(poiItem.getTag(), item);
            }

            mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds));

            MapPOIItem[] poiItems = mapView.getPOIItems();
            if (poiItems.length > 0) {
                mapView.selectPOIItem(poiItems[0], false);
            }
        }

    public void onButtonPressed(String point) {
        if (mListener != null) {
            mListener.onFragmentInteraction(point);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {
        Log.i(TAG, "onMapViewInitialized");


        MyCustomBalloonAdapter customBallon = new MyCustomBalloonAdapter();
        //이전에 방문했던 지역을 지도에 표시.
        mapView.setCalloutBalloonAdapter(customBallon);

        getAllMapPointsAndMakeMarker(mapView);
    }

    private void getAllMapPointsAndMakeMarker(MapView mapView) {

        String tempMapPoint;
        String[] mapPoint;
        String title;
        String desc;

        Log.d(TAG, "Get All stored MapPoints");
        List<Board> boards = BoardLab.getBoardLab(getActivity()).getBoards();

        for (Board singleBoard: boards) {
            tempMapPoint = singleBoard.getMapPoint();
            mapPoint = tempMapPoint.split("/");
            title = singleBoard.getTitle();
            AllForMarker(mapView, mapPoint, title);
        }
    }
    /**
     * 지도에 마커를 꼭 찍는 함수.
     * @param mapView
     * @param points
     * @param title
     */
    private void AllForMarker(MapView mapView, String[] points, String title) {
        mCustomMarker = new MapPOIItem();
        mCustomMarker.setItemName(title);
        mCustomMarker.setTag(1);


        double latitude = Double.parseDouble(points[0]);
        double longtitude = Double.parseDouble(points[1]);

        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longtitude);
        mCustomMarker.setMapPoint(mapPoint);
        mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        mCustomMarker.setCustomImageResourceId(R.drawable.custom_marker_red);
        mCustomMarker.setCustomImageAutoscale(false);
        mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);

        mapView.addPOIItem(mCustomMarker);
        mapView.selectPOIItem(mCustomMarker, true);
        mapView.setMapCenterPoint(mapPoint, false);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

        //이전에 눌렀던 마커는 삭제되게...
            mapView.removeAllPOIItems();
            mapView.setCalloutBalloonAdapter(new MyCustomBalloonAdapter());
            createCustomMarker(mapView, mapPoint);
            showAll(mapPoint);
            mMapPoint = mapPoint;
            Log.d(TAG, "Insert Complete MapPoint" + mapPoint.getMapPointGeoCoord().longitude + "/" + mapPoint.getMapPointGeoCoord().latitude);
    }

    /**
     *  필요 따라 선택해서 만들자
     *  아래 마커는 가장 기본적인 마커
     * @param mapView
     * @param point
     */
    private void createDefaultMarker(MapView mapView, MapPoint point) {
        mDefaultMarker = new MapPOIItem();
        String name = "Default Marker";
        mDefaultMarker.setItemName(name);
        mDefaultMarker.setTag(0);
        mDefaultMarker.setMapPoint(point);
        mDefaultMarker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        mDefaultMarker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

        mapView.addPOIItem(mDefaultMarker);
        mapView.selectPOIItem(mDefaultMarker, true);
        mapView.setMapCenterPoint(point, false);
    }

    private MapPOIItem createCustomMarker(MapView mapView, MapPoint point) {

        mCustomMarker = new MapPOIItem();
        String name = "Click";
        mCustomMarker.setItemName(name);
        mCustomMarker.setTag(1);
        mCustomMarker.setMapPoint(point);
        mCustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage);
        mCustomMarker.setCustomImageResourceId(R.drawable.custom_marker_red);
        mCustomMarker.setCustomImageAutoscale(false);
        mCustomMarker.setCustomImageAnchor(0.5f, 1.0f);

        mapView.addPOIItem(mCustomMarker);
        mapView.selectPOIItem(mCustomMarker, true);
        mapView.setMapCenterPoint(point, false);
        return mCustomMarker;
    }

    private void showAll(MapPoint point) {
        int padding = 20;
        float minZoomLevel = 1;
        float maxZoomLevel = 10;
        MapPointBounds bounds = new MapPointBounds(point, point);
        mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(bounds, padding, minZoomLevel, maxZoomLevel));
    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(final MapView mapView, final MapPOIItem mapPOIItem) {
        //TODO 해당 좌표를 대쉬보드로 넘기고 여기에서 했던 일들을 기록.
        Log.d(TAG, "말머리표 버튼을 눌렀습니다");
        // 꼬리꼬리 꼬리에 해당하는 것임.!

        String mapPoint =  mapPOIItem.getMapPoint().getMapPointGeoCoord().latitude + "/" + mapPOIItem.getMapPoint().getMapPointGeoCoord().longitude;

        if (hasBoard(mapPoint)){

            Board board = getBoardByMapPoint(mapPoint);
            FragmentManager manager = getFragmentManager();
//                DatePickerFragment dialog = new DatePickerFragment();
            ConfirmPlaceFragment dialog =
                    ConfirmPlaceFragment.newInstance(board.getUUID().toString(), board.getTitle(), board.getDescription());
            //Fragment간 데이터 전달할떄 연결하는 메소드
            dialog.show(manager, DIALOG_CONFIRM);

        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.map_review_title)
                    .setMessage(R.string.map_confirm_review)
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            Intent confirmIntent = new Intent(getActivity(), AddBoardActivity.class);
                            Intent confirmIntent = AddBoardActivity.newIntent(getActivity(), null);
                            String latitude = mMapPoint.getMapPointGeoCoord().latitude + "";
                            String longitude = mMapPoint.getMapPointGeoCoord().longitude + "";
                            //이렇게 넘기는 저장하기 위해서...
                            confirmIntent.putExtra("latitude", latitude);
                            confirmIntent.putExtra("longitude", longitude);
                            startActivityForResult(confirmIntent, CONFIRM_REQUEST);
                            Log.d(TAG, "확인 버튼 누름");
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Log.d(TAG, "취소 버튼 누름");
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    public Boolean hasBoard(String point){

        String tempMapPoint;

        List<Board> boards = BoardLab.getBoardLab(getActivity()).getBoards();

        for (Board singleBoard: boards) {
            tempMapPoint = singleBoard.getMapPoint();
            if (tempMapPoint.equals(point)){
                return true;
            }
        }
        return false;
    }

    private Board getBoardByMapPoint(String point){

        String tempMapPoint;

        List<Board> boards = BoardLab.getBoardLab(getActivity()).getBoards();

        for (Board singleBoard: boards) {
            tempMapPoint = singleBoard.getMapPoint();
            if (tempMapPoint.equals(point)){
                return singleBoard;
            }
        }
        return null;
    }


    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {
        Log.d(TAG, "onCalloutBalloonOfPOIItemTouched");
        mapView.refreshMapTiles();

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {
        Log.d(TAG, "onDraggablePOIItemMoved");
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edit_Search.getWindowToken(), 0);
    }

    private Drawable createDrawableFromUrl(String url) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Object fetch(String address) throws MalformedURLException,IOException {
        URL url = new URL(address);
        Object content = url.getContent();
        return content;
    }
}



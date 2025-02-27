let idx; // 일정 번호
let writer; // 일정 작성자
let title; // 일정 이름
let departure; // 일정 출발일
let days; // 일정 기간
let plans; // 일정 관광지들
let good; // 일정 좋아요 개수
let hit; // 일정 조회수
let pub; // 일정 공개여부
let plan; // 일정 전체 데이터

let username; // 접속자 이름
let alreadyGood; // 이전 좋아요 여부
let spotsOfDays = []; // 날짜별 관광지 세부정보들
let chooseDay = 1; // 현재 선택한 날짜
let dayMoveLength; // 하루 총 이동거리
let beforeUrl; // 이전 페이지 경로

$(document).ready(function () {
    $('#detail-view').hide();
    initVar();

    getPlans(idx).then(response => {
        initPage(response);
        // printSpotsInfo();
        getSpotsDetails(response).then(() => {
            printSpotsInfo();
            printDaysforMap();
            createMap(1);
        });
    });

});

// 컨트롤러에서 Model에 넘긴 값 변수에 할당
function initVar() {
    beforeUrl = window.location.href;
    idx = $('#hiddenIdx').val();
    username = $('#hiddenUsername').val();
    alreadyGood = $('#hiddenIsAlreadyGood').val();
}

// 일정 정보 가져오기 (Ajax)
function getPlans(idx) {
    return new Promise((resolve => {
        $.ajax({
            url: `/plan/${idx}`,
            method: 'get',
            data: {'idx': idx},
            dataType: 'json',
            success: function (response) {
                plan = response;
                resolve(response);
            },
            error: function () {
                alert("getPlans");
            }
        })
    }));
}

// 가져온 일정 데이터로 초기화면 구성
function initPage(response) {
    writer = response.username;
    title = response.title;
    departure = response.departure;
    days = response.days;
    plans = JSON.parse(response.plans);
    good = response.good;
    hit = response.hit;
    pub = response.pub;

    $('#plan-title').text(title);
    $('#user-name').text(writer);

    let depDate = new Date(departure);
    let lastDate = new Date(departure);
    lastDate.setDate(lastDate.getDate() + days - 1);
    $('#plan-date').text(dateFormatter(depDate) + " ~ " + dateFormatter(lastDate));
    $('#plan-days').text(days == 1 ? "당일치기" : "(" + (days - 1) + "박 " + days + "일)");

    if (pub == 0) {
        $('#pub span').html("비공개");
    }

    $('#good span:nth-child(2)').text(good);
    $('#hit span:nth-child(2)').text(hit);

    if (alreadyGood == "true") {
        $('#heart').css({"fontWeight": "bolder"});
    }
}

// 일정의 관광지 세부정보 가져오기 (Ajax)
function getSpotsDetails(response) {
    let plans = JSON.parse(response.plans);
    return new Promise((resolve => {
        (async () => {
            for (let i = 0; i < plans.length; i++) {
                await $.ajax({
                    url: `/spot/list/titles`,
                    method: 'get',
                    data: {"titles": JSON.stringify(plans[i])},
                    dataType: 'json',
                    success: function (response) {
                        spotsOfDays[i] = response;
                    },
                    error: function () {
                        alert("getSpotsDetail");
                    }
                })
            }
            resolve(spotsOfDays);
        })();
    }));
}

// 일정의 관광지 출력
function printSpotsInfo() {
    for (let i = 0; i < spotsOfDays.length; i++) {
        $('#plans').append(
            `<div class='day-header'>Day${i + 1}</div>`
        );
        for (let j = 0; j < spotsOfDays[i].length; j++) {
            let spot = spotsOfDays[i][j];
            $('#plans').append(
                `<div class='day-spots' id='day${i + 1}-spots${j + 1}' onclick="viewSpotDetail(spotsOfDays[${i}][${j}])">` +
                `  <img src='${spot.photo}'>` +
                `  <div class='spot-title'>${spot.title}</div>` +
                `</div>`
            );
        }
        if (spotsOfDays[i].length == undefined) {
            $('#plans').append(
                `<div class="none-spots"><div>일정이 없습니다.</div></div>`
            );
        }
    }
}

// 날짜 선택 버튼 출력
function printDaysforMap() {
    for (let i = 0; i < spotsOfDays.length; i++) {
        $('#days').append(
            `<div class="day" onclick="createMap(${i + 1})">Day${i + 1}</div>`
        );
    }
}

// 지도 생성
function createMap(day) {
    chooseDay = day;
    $(`.day`).css({'color': 'black'});
    $(`.day:nth-child(${day})`).css({'color': 'rgba(217,117,22,0.8)'});

    $('#detail-view').hide();
    $('#map').show();

    let container = document.getElementById('map');
    let options = {
        center: new kakao.maps.LatLng(33.380690, 126.545383),
        level: 10
    };

    map = new kakao.maps.Map(container, options);

    // 지도 타입 컨트롤
    let mapTypeControl = new kakao.maps.MapTypeControl();
    map.addControl(mapTypeControl, kakao.maps.ControlPosition.TOPRIGHT);

    // 줌 컨트롤
    let zoomControl = new kakao.maps.ZoomControl();
    map.addControl(zoomControl, kakao.maps.ControlPosition.RIGHT);

    createMarkerAndLine(day);
}

// 마커, 라인 생성
function createMarkerAndLine(day) {
    let points = [];
    let marker = [];
    let linePath = [];

    for (let i = 0; i < spotsOfDays[day - 1].length; i++) {

        let daySpots = spotsOfDays[day - 1][i];
        points.push(new kakao.maps.LatLng(daySpots.lat, daySpots.lng));
        marker.push(new kakao.maps.Marker({position: points[i]}));
        marker[i].setMap(map);

        // 선 그을 위치 정보 저장
        linePath.push(new kakao.maps.LatLng(daySpots.lat, daySpots.lng));

        // 인포윈도우 생성
        let iwContent = '<div style="padding:5px; overflow: hidden; white-space: nowrap; font-size: 14px;">' + (i + 1) + '. ' + daySpots.title + '</div>';

        let infowindow = new kakao.maps.InfoWindow({
            content: iwContent
        });

        // 마커 클릭 이벤트 : 관광지 세부정보 표시
        kakao.maps.event.addListener(marker[i], 'click', function () {
            viewSpotDetail(daySpots);
        });

        // 마커 마우스오버 이벤트 : 인포윈도우 표시
        kakao.maps.event.addListener(marker[i], 'mouseover', function () {
            infowindow.open(map, marker[i]);
        });

        // 마커 마우스아웃 이벤트 : 인포윈도우 제거
        kakao.maps.event.addListener(marker[i], 'mouseout', function () {
            infowindow.close();
        });
    }

    let polyline = new kakao.maps.Polyline({
        path: linePath, // 선을 구성하는 좌표배열 입니다
        strokeWeight: 5, // 선의 두께 입니다
        strokeColor: '#d77045', // 선의 색깔입니다
        strokeOpacity: 0.9, // 선의 불투명도 입니다 1에서 0 사이의 값이며 0에 가까울수록 투명합니다
        strokeStyle: 'solid' // 선의 스타일입니다
    });
    dayMoveLength = Math.round(polyline.getLength() / 100) / 10;
    polyline.setMap(map);
    printDayInfo(day);
}

// 선택일 정보 출력
function printDayInfo(day) {
    $('#choose-day').text(`Day${chooseDay}`)

    let date = new Date(plan.departure);
    date.setDate(date.getDate() + day - 1);
    $('#day-date').text(dateFormatter(date));

    let spotsCount = spotsOfDays[chooseDay - 1].length;
    if (spotsCount == null) {
        spotsCount = 0;
    }
    $('#day-spots-count').text(`일정 개수 : ${spotsCount}개`);
    $('#day-move-length').text(`이동거리 : 약 ${dayMoveLength}km`);
}

// 지도 뷰에서 디테일 뷰로 전환
function viewSpotDetail(spot) {
    $('#map').hide();
    $('#detail-view').empty();

    $('#detail-view').append(
        `<img src="${spot.photo}">` +
        `<div id="detail-title">${spot.title}</div>` +
        `<div id="detail-info">${spot.info}</div>` +
        `<div id="close" onclick="detailToMap()">X</div>`
    );

    $('#detail-view').show();
}

// 디테일 뷰에서 지도 뷰로 전환
function detailToMap() {
    $('#detail-view').empty();
    $('#detail-view').hide();
    $('#map').show();
}

// 좋아요 변경 (Ajax)
function goodToggle(idx, username) {
    $.ajax({
        url: `/plan/${idx}/good`,
        method: 'post',
        data: {'username': username, 'beforeUrl': beforeUrl},
        success: function (response) {
            if (response == "guest") {
                location.replace('/loginPage');
            } else if (response == "true") {
                good = good - 1;
                $('#good span:nth-child(2)').text(good);
                $('#heart').css({"fontWeight": "normal"});
            } else if (response == "false") {
                good = good + 1;
                $('#good span:nth-child(2)').text(good);
                $('#heart').css({"fontWeight": "bolder"});
            }
        }
    })
}

// 날짜 포맷 변환
function dateFormatter(date) {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return year + '-' + month + '-' + day;
}
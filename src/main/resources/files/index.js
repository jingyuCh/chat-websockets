let userName = ""; //用於儲存使用者名稱




var socket = new WebSocket("ws://localhost:8080/chat",);

function init() {
    //Step1:取得index.html中的元件
    const title = document.getElementById('title');
    const form = document.getElementById('form');
    const input = document.getElementById('input');

    //Step2:顯示輸入框讓使用者輸入名稱，並修改標題內容
    userName = prompt('請輸入你的名稱');
    if (!userName || userName == '') {
        title.textContent = '請重新載入網頁並輸入你的名稱';
        return;
    } else {
        title.textContent = `Hi, ${userName}, 歡迎來到WebSocket聊天室`;
    }

    //Step3:設定元件的監聽事件
    form.addEventListener('submit', function(e) {
        e.preventDefault();
             var values = {};
             values.type ='message';
             values.message = input.value;
             values.userName = userName;
             socket.send(JSON.stringify(values));
            input.value = ''; //清空輸入的訊息內容
//        }
    });
    socket.onmessage = function(e) {
    var values = JSON.parse(e.data);
       //showMsg(`${name} 說：`+e.data); //顯示新訊息
       if(values.type == 'join'){
            showMsg(`【${values.userName} 進入聊天室】`);
       }
       if(values.type == 'message'){
             showMsg(`${values.userName} 說：${values.message}`); //顯示新訊息
       }
       if(values.type == 'close'){
             showMsg(`【${values.userName} 離開聊天室】`);
       }
    }

    socket.addEventListener('open', () => {
        var values = {};
        values.type ='join';
        values.message ='';
        values.userName = userName;

        socket.send(JSON.stringify(values));
    })

    socket.addEventListener('close', () => {
            var values = {};
            values.type ='close';
            values.message ='';
            values.userName = userName;

            socket.send(JSON.stringify(values));
        })
}

function showMsg(text) {
    //建立用於顯示訊息的清單項目元件
    const item = document.createElement('li');
    item.textContent = text;
    //將清單項目元件呈現於訊息列表
    const messages = document.getElementById('messages');
    messages.appendChild(item);
    //滾動置底
    window.scrollTo(0, document.body.scrollHeight);
}

init();
function getCookie(cname) {
    let name = cname + "=";
    let ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) == ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return null;
}

let stompClient = null;

function setConnected(connected) {

}

function connect() {
    stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/ws',
        connectHeaders: {
            'X-XSRF-TOKEN': getCookie("XSRF-TOKEN")
        }
    });
    stompClient.onConnect = (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/chat/message', (greeting) => {
            showGreeting(JSON.stringify(JSON.parse(greeting.body)));
        });
        stompClient.subscribe('/user/chat/history', (greeting) => {
            showGreeting(JSON.stringify(JSON.parse(greeting.body)));
        });
        stompClient.subscribe('/user/chat/status', (greeting) => {
            showGreeting(JSON.stringify(JSON.parse(greeting.body)));
        });
        stompClient.subscribe('/user/chat/update', (greeting) => {
            showGreeting(JSON.stringify(JSON.parse(greeting.body)));
        });
    };
    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };
    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };
    stompClient.reconnect_delay = 1000;
    stompClient.activate();
}

function showGreeting(message) {
    $("#messages").append(`<p>${message}</p>`);
}

$(function () {
    $("#send").click(() => sendMessage());
    $("#messageInput").on("keydown", handleEnter)
});


function handleEnter(event) {
    if (event.key === "Enter") {
        sendMessage();
    }
}

let peerId = null

const randomNumber = (min, max) => {
    const scope = max - min + 1;
    return Math.floor(min + scope * Math.random());
};

const randomLong = () => {
    const a = randomNumber(-922337203685477, +922337203685477);
    if (a < 0) {
        return a + randomString(0, a > -922337203685477 ? 10000 : 5808, 4);
    } else {
        return a + randomString(0, a < +922337203685477 ? 10000 : 5807, 4);
    }
};

const randomString = (min, max, length = 0) => {
    let text = String(randomNumber(min, max));
    for (let i = text.length; i < length; ++i) {
        text = '0' + text;
    }
    return text;
};

function sendMessage() {
    if ($("#messageInput").val().trim() !== "") {
        stompClient.publish({
            destination: "/app/chat/message",
            body: JSON.stringify(
                {
                    'text': $("#messageInput").val(),
                    'peerId': 2,
                    'randomId': randomLong()
                })
        });

        stompClient.publish({
            destination: "/app/chat/history",
            body: JSON.stringify(
                {
                    'peerId': 2,
                    'limit': 20
                })
        });

        $("#messageInput").val("")
    }
}

const createChatIfNotExists = (peerId) => {
}


connect()
let peerId = 2
let peers = {}
let stompClient = null;
let connectionStatus = false

const setConnected = (connected) => {

}

const handleUpdate = (json) => {
    printMessage(JSON.stringify(json));
}

const handleError = (json) => {
    printMessage(JSON.stringify(json));
}

const connect = () => {
    stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/ws',
        connectHeaders: {
            'X-XSRF-TOKEN': getCookie("XSRF-TOKEN")
        }
    });
    stompClient.onConnect = (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/topic/update', (update) => {
            handleUpdate(JSON.parse(update.body));
        });
        stompClient.subscribe('/user/topic/error', (error) => {
            handleError(JSON.parse(error.body));
        });
    };
    stompClient.onWebSocketError = (error) => {
        setConnected(false);
        console.error('Error with websocket', error);
    };
    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };
    stompClient.reconnect_delay = 1000;
    stompClient.activate();
}

function printMessage(message) {
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

const sendMessage = () => {
    if (peerId != null) {
        const input = $("#messageInput")
        if (input.val().trim() !== "") {
            stompClient.publish({
                destination: "/app/chat/message",
                body: JSON.stringify(
                    {
                        'text': input.val(),
                        'peerId': peerId,
                        'randomId': randomLong()
                    })
            });
            input.val("")
        }
    }
}

const getCookie = (cname) => {
    let name = cname + "=";
    let ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') {
            c = c.substring(1);
        }
        if (c.indexOf(name) === 0) {
            return c.substring(name.length, c.length);
        }
    }
    return null;
}

connect()
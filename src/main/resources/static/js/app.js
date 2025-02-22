let peerId = null
let peers = {}
let stompClient = null;
let searchCategory = "user"

const setConnected = (connected) => {
    const con = document.getElementById("connection")
    if (connected) {
        con.innerText = "Connected"
    } else {
        con.innerText = "Connecting..."
    }
}

const save = (message) => {
    if (!peers[message.peerId]) peers[message.peerId] = {messages: []};
    peers[message.peerId].messages.push(message);
}

async function userSearchRequest(peerId) {
    const response = await fetch(window.location.origin + `/api/users/` + peerId);
    if (response.status === 200) {
        const user = renderUser(await response.json())
        document.getElementById("chats").appendChild(user)
    }
}

const addChatIfNotExists = (peerId) => {
    const peer = document.getElementById("chats").querySelector(`[data-peer-id="${peerId}"]`)
    if (!peer) {
        if (parseInt(peerId) > 0) {
            userSearchRequest(peerId);
        }
    }
};

const handleUpdate = (json) => {
    save(json)
    addChatIfNotExists(json.peerId)
    if (json.peerId.toString() === peerId) {
        const container = document.getElementById("chat-messages")
        appendChildAndScroll(container, getMessage(json))
    }
}

function appendChildAndScroll(container, newChild) {
    const isScrolledToBottom = container.scrollHeight - container.scrollTop === container.clientHeight;

    container.appendChild(newChild);

    if (isScrolledToBottom) {
        container.scrollTop = container.scrollHeight;
    }
}

const handleError = (json) => {
    console.log(JSON.stringify(json))
}

let statusTimer;
const handleStatus = (json) => {
    clearTimeout(statusTimer)

    statusTimer = setTimeout(() => {
        const ids = new Set(json.ids);
        const peers = document.querySelectorAll(`[data-peer-id]`)
        peers.forEach(function (peerElement) {
                const currentPeerId = parseInt(peerElement.dataset.peerId)
                if (currentPeerId > 0) {
                    const statusElement = peerElement.querySelector('.small');
                    if (ids.has(currentPeerId)) {
                        statusElement.textContent = 'ONLINE';
                    } else {
                        statusElement.textContent = 'OFFLINE';
                    }
                }
            }
        );
    }, 500)
}

const connect = () => {
    setConnected(false)
    stompClient = new StompJs.Client({
        brokerURL: `ws://${window.location.host}/ws`,
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
        stompClient.subscribe('/topic/status', (status) => {
            handleStatus(JSON.parse(status.body));
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

function getMessage(message) {
    const div = document.createElement("div");
    div.classList.add("pb-4");

    div.classList.add(message.outgoing ? "chat-message-right" : "chat-message-left");

    const time = document.createElement("div");
    time.classList.add("text-muted", "small", "text-nowrap", "mt-2");
    time.innerText = parseDate(message.createdAt);

    const timeDiv = document.createElement("div");
    timeDiv.appendChild(time);

    const userNameDiv = document.createElement("div");
    userNameDiv.classList.add("font-weight-bold", "mb-1");
    userNameDiv.innerText = message.outgoing ? "You" : message.senderName;

    const contentDiv = document.createElement("div");
    contentDiv.innerText = message.text;

    const textDiv = document.createElement("div");
    textDiv.classList.add("flex-shrink-1", "bg-light", "rounded", "py-2", "px-3", "mr-3");
    textDiv.append(userNameDiv, contentDiv, timeDiv);

    div.append(textDiv);

    return div;
}


const options = {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    hour12: false
};

const parseDate = (iso) => {
    return new Date(iso)
        .toLocaleString("en-US", options)
        .replace(",", "");
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
                    })
            });
            input.val("")
            const container = document.getElementById("chat-messages")
            container.scrollTop = container.scrollHeight;
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

const handleSearchClick = () => {
    const chats = document.getElementById("chats");
    const search = document.getElementById("search");
    const cancel_search = document.getElementById("cancel-search");

    search.hidden = false
    chats.hidden = true
    cancel_search.style.visibility = "visible"
}

let timeout;
const handleSearchInput = () => {
    clearTimeout(timeout);

    const value = document.getElementById("search-input").value
    if (value !== "") {
        timeout = setTimeout(() => {
            searchRequest(value);
        }, 500);
    }
}

const searchRequest = (value) => {
    if (searchCategory === "user") {
        usersSearchRequest(value);
    } else {
        groupsSearchRequest(value);
    }
}

async function usersSearchRequest(value) {
    const response = await fetch(window.location.origin + `/api/users?name=${value}`);
    if (response.status === 200) {
        renderUsers(await response.json())
    } else {
        renderUsers({users: []})
    }
}

const renderUsers = (json) => {
    const search_result = document.getElementById("search-result");
    search_result.innerHTML = ''
    for (const i in json.users) {
        console.log(json.users[i])
        search_result.appendChild(renderUser(json.users[i]))
    }
}

const renderUser = (user) => {
    const a = document.createElement("a");
    a.href = "#";
    a.dataset.peerId = user.peerId;
    a.dataset.peerName = user.name;
    a.classList.add("list-group-item", "list-group-item-action", "border-0");

    const container = document.createElement("div");
    container.classList.add("d-flex", "align-items-start");

    const content = document.createElement("div");
    content.classList.add("flex-grow-1", "ml-3");
    content.textContent = user.name;

    const smallText = document.createElement("div");
    smallText.classList.add("small");
    smallText.textContent = user.status;

    content.appendChild(smallText);
    container.appendChild(content);
    a.appendChild(container);

    a.onclick = () => handlePeerClick(a)

    return a;
};

const loadHistory = (peerId) => {
    const container = document.getElementById("chat-messages")
    container.innerText = ""

    if (peers[peerId]) {
        peers[peerId].messages.forEach((message) => {
                container.appendChild(getMessage(message))
                offsetId = message.id
            }
        )
        container.scrollTop = container.scrollHeight;
        if (peers[peerId].messages.length < 10) {
            historyRequest(peerId)
            container.scrollTop = container.scrollHeight;
        }
    } else {
        historyRequest(peerId)
        container.scrollTop = container.scrollHeight;
    }
};

let historyTimer;
const handleSearchOnScroll = (element) => {
    if (element.scrollTop === 0) {
        clearTimeout(historyTimer)
        historyTimer = setTimeout(() => {
            historyRequest(peerId)
        }, 100)
    }
}

let notEnd = true
let offsetId = null

async function historyRequest(peerId) {
    const container = document.getElementById("chat-messages")
    if (notEnd) {
        const url = new URL(window.location.origin + "/api/messages");
        url.searchParams.append("peerId", peerId);
        if (offsetId) {
            url.searchParams.append("offsetId", offsetId);
        }
        const response = await fetch(url);
        if (response.status === 204) {
            peers[peerId].notEnd = false
            notEnd = false
        }
        if (response.status === 200) {
            const json = await response.json()
            json.messages.forEach((message) => {
                save(message)
                const scrollTop = container.scrollTop;
                const m_div = getMessage(message)
                container.prepend(m_div)
                container.scrollTop = scrollTop + m_div.clientHeight;
                offsetId = message.id
            })
        }
    }
}

async function leaveGroupRequest() {
    const response = await fetch(window.location.origin + `/api/groups/` + peerId + "/members", {method: "DELETE"});
    if (response.status === 200) {
        document.getElementById("name").innerText = ""
        document.getElementById("chat-messages").innerText = ""
        document.getElementById("chats").querySelector(`[data-peer-id="${peerId}"]`).remove()
        peers[peerId] = null
        peerId = null
        offsetId = null
        notEnd = false
    }
}

async function participateInGroupRequest() {
    const response = await fetch(window.location.origin + `/api/groups/` + peerId + "/members", {method: "POST"});
    if (response.status === 200) {
        const button = document.getElementById("name").querySelector("button")
        button.innerText = "Покинуть группу"
        button.onclick = leaveGroupRequest
        const group = renderGroup(await response.json())
        document.getElementById("chats").appendChild(group)
        loadHistory(peerId)
    }
}

const handlePeerClick = (element) => {
    handleSearchBlur()
    peerId = element.dataset.peerId
    notEnd = peers[peerId] ? peers[peerId].hasOwnProperty("notEnd") ? peers[peerId].notEnd : true : true
    offsetId = null
    document.getElementById("name").innerText = element.dataset.peerName
    if (parseInt(peerId) < 0) {
        const button = document.createElement("button")
        if (element.dataset.isMember === "true") {
            button.innerText = "Покинуть группу"
            button.onclick = leaveGroupRequest
        } else {
            button.innerText = "Вступить в группу"
            button.onclick = participateInGroupRequest
        }
        document.getElementById("name").appendChild(button)
        if (element.dataset.isMember) {
            loadHistory(peerId)
            return
        }
    }
    loadHistory(peerId)
}

async function groupsSearchRequest(value) {
    const response = await fetch(window.location.origin + `/api/groups?name=${value}`);
    if (response.status === 200) {
        renderGroups(await response.json())
    } else {
        renderGroups({groups: []})
    }
}

const renderGroups = (json) => {
    const search_result = document.getElementById("search-result");
    search_result.innerHTML = ''
    for (const i in json.groups) {
        search_result.appendChild(renderGroup(json.groups[i]))
    }
}

const renderGroup = (group) => {
    const a = document.createElement("a");
    a.href = "#";
    a.dataset.peerId = group.peerId;
    a.dataset.peerName = group.name;
    a.dataset.isMember = group.isMember;
    a.classList.add("list-group-item", "list-group-item-action", "border-0");

    const container = document.createElement("div");
    container.classList.add("d-flex", "align-items-start");

    const content = document.createElement("div");
    content.classList.add("flex-grow-1", "ml-3");
    content.textContent = group.name;

    const smallText = document.createElement("div");
    smallText.classList.add("small");
    smallText.textContent = "Группа";

    content.appendChild(smallText);
    container.appendChild(content);
    a.appendChild(container);

    a.onclick = () => handlePeerClick(a)

    return a;
};

const handleSearchBlur = () => {
    const chats = document.getElementById("chats");
    const search = document.getElementById("search");
    const cancel_search = document.getElementById("cancel-search");
    const search_input = document.getElementById("search-input");
    const search_result = document.getElementById("search-result");

    chats.hidden = false
    search.hidden = true
    cancel_search.style.visibility = "hidden"
    search_input.value = ""
    search_result.innerText = ""
}

const handleUsersCategoryClick = () => {
    const users = document.getElementById("users-category");
    const groups = document.getElementById("group-category");

    users.classList.add('selected');
    users.classList.remove('not-selected');
    groups.classList.add('not-selected');
    groups.classList.remove('selected');

    searchCategory = "user"
    const value = document.getElementById("search-input").value
    if (value !== "") {
        searchRequest(value);
    }
}

const handleGroupsCategoryClick = () => {
    const users = document.getElementById("users-category");
    const groups = document.getElementById("group-category");

    users.classList.remove('selected');
    users.classList.add('not-selected');
    groups.classList.add('selected');
    groups.classList.remove('not-selected');

    searchCategory = "group"
    const value = document.getElementById("search-input").value
    if (value !== "") {
        searchRequest(value);
    }
}

const handleCreateGroup = () => {
    const group_input = document.getElementById("group-input");
    const value = group_input.value
    if (value !== "") {
        createGroupRequest(value)
        group_input.innerText = ""
    }
}

async function createGroupRequest(value) {
    const response = await fetch(window.location.origin + `/api/groups?name=${value}`, {method: "POST"});
    if (response.status === 201) {
        const group = renderGroup(await response.json())
        document.getElementById("chats").appendChild(group)
    }
}

connect()
$(function(){
    let lastMessagesResponse = '';
    let lastUsersResponse = '';

    let getMessageElement = function(message) {
        let item = $('<div class="message-item"></div>');
        let header = $('<div class="message-header"></div>');
        header.append($('<div class="datetime">' +
            message.datetime + '</div>'));
        header.append($('<div class="username">' +
            message.username + '</div>'));
        let textElement = $('<div class="message-text"></div>');
        textElement.text(message.text);
        item.append(header, textElement);
        return item;
    };

    let updateMessages = function() {
        $.get('/api/message', {}, function(response){
            if (response.length == 0) {
                $('.messages-list').html('<i>Сообщений нет</i>');
                return;
            }
            let responseString = JSON.stringify(response);
            if (lastMessagesResponse == responseString) {
                return;
            }
            $('.messages-list').html('');
            for (i in response) {
                let element = getMessageElement(response[i]);
                $('.messages-list').append(element);
            }
            lastMessagesResponse = responseString;
        });
    };

    let updateUsers = function() {
            $.get('/api/users', {}, function(response){
                if (response.length == 0) {
                    $('.users-list').html('<i>Пользователей нет</i>');
                    return;
                }
                let responseString = JSON.stringify(response);
                if (lastUsersResponse == responseString) {
                    return;
                }
                $('.users-list').html('');
                for (i in response) {
                    let element = $('<div class="user-item">' + response[i] + '</div>');
                    $('.users-list').append(element);
                }
                lastMessagesResponse = responseString;
            });
        };

    let initApplication = function() {
        $('.messages-and-users').css({display: 'flex'});
        $('.controls').css({display: 'flex'});

        $('.send-message').on('click', function() {
            let message = $('.new-message').val();
            $.post('/api/message', {message: message}, function(response){
                if(response.result) {
                    $('.new-message').val('');
                } else {
                    alert('Ошибка :( Повторите попытку позже');
                }
            });
        });

        setInterval(updateMessages, 1000);
        setInterval(updateUsers, 1000);
    };

    let registerUser = function(name) {
        $.post('/api/auth', {name: name}, function(response){
            if(response.result) {
                initApplication();
            }
        });
    };

    $.get('/api/init', {}, function(response){
        if(!response.result)
        {
            let name = prompt('Введите Ваше имя:');
            registerUser(name);
            return;
        }
        initApplication();
    });


});
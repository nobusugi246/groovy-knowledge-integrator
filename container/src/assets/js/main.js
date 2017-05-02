(function () {
    console.log('main started.')

    $('body').removeClass('uk-invisible')

    var app = new Vue({
        el: '#yourName',
        data: {
            message: 'Hello Vue!'
        },
        methods: {
            reverseMessage: function () {
                this.message = this.message.split('').reverse().join('')
            }
        }
    })

    $('#newBotButton').on('click', function (e) {
        e.preventDefault();
        $(this).blur();
        UIkit.modal.confirm('UIkit confirm!').then(function () {
            console.log('Confirmed.')
        }, function () {
            console.log('Rejected.')
        });
    });
})();
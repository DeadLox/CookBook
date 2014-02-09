/**
 * CookBook
 * By DeadLox
 */
$(document).ready(function(){
    $('.infoBulle').tooltip({placement: 'auto'});
    tinymce.init({
        selector: "textarea",
        language : 'fr_FR',
        height : 300
    });
});
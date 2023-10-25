$(document).ready(function() {
    $('#summernote').summernote({
        callbacks: {
            height: 200,
            onImageUpload: function(files, editor, welEditable) {
                sendFile(files[0], this);
            }
        }
    });

    function sendFile(file, editor) {
        data = new FormData();
        data.append("file", file);
        $.ajax({
            data: data,
            type: "POST",
            url: "/image/upload",
            cache: false,
            contentType: false,
            enctype: 'multipart/form-data',
            processData: false,
            success: function(url) {
                $(editor).summernote('insertImage', url, function(image){

                });
            }
        });
    }
});




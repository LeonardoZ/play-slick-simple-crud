(function($){
  var mySelectedDate = new Date();
		$('#confirm-delete').on('show.bs.modal', function(e) {
		  var data = $(e.relatedTarget).data();
          $('.title', this).text(data.name);
          $('#btn-delete', this).attr('data-id', data.id);
        });

        $('#confirm-completed').on('show.bs.modal', function(e) {
		  var data = $(e.relatedTarget).data();
          $('.title', this).text(data.name);
          $('#btn-complete', this).attr('data-id', data.id);
        });

        $('#date-time-picker').datetimepicker({
          	format : "DD/MM/YYYY HH:mm",
			locale : 'pt-br'
        });

        $('#date-time-picker').on('dp.change', function(newDate, oldDate) {
            if (newDate != null) {
                $('#btn-complete').prop('disabled', '');
                mySelectedDate = newDate;
            } else {
                $('#btn-complete').prop('disabled', 'disabled');
            }
        });

        $('#txt-datetime').on('keyup blur', function () {
            var content = $(this).val();
            if (content) {
                 $('#btn-complete').prop('disabled', '');
            } else {
                $('#btn-complete').prop('disabled', 'disabled');
            }
        });

      $('#btn-delete').on('click', function(e) {
          var id = $(this).attr('data-id');
          var $modalDiv = $('#confirm-delete');
          $.post('/delete/'+id).then(function() {
             $('#task-'+id).remove();
             $modalDiv.modal('hide');
          });
      });

        $('#btn-complete').on('click', function(e){
          var id = $(this).attr('data-id');
          var $modalDiv = $(e.delegateTarget);
          $.ajax({
              url: '/completeTask',
              method: 'POST',
              data: JSON.stringify({
                'id' : id,
                'when' : mySelectedDate.date._d
              }),
              contentType: 'application/json'
          }).done(function(res) {
               var when = new Date(res.completedWhen);
               when = new Date(when.valueOf() + when.getTimezoneOffset() * 60000)
               var n = format(when);
               $('#completed-'+res.id).html(n);
               $('#confirm-completed').modal('hide');
          });

          function format(when) {
               var y = putDecimal(when.getFullYear());
               var m = putDecimal(when.getMonth() + 1);
               var d = putDecimal(when.getDate());
               var h = putDecimal(when.getHours());
               var mi = putDecimal(when.getMinutes());
               var s = putDecimal(when.getSeconds());
               return  y + '-' + m + '-' + d + ' ' + h + ':' + mi + ':' + s;
          };

          function putDecimal(i) {
            return i < 9 ? ("0" + i) : i;
          }
        });

})(jQuery);
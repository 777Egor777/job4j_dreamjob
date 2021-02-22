<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="ru.job4j.dream.model.Candidate" %>
<%@ page import="ru.job4j.dream.store.PsqlCandidateStore" %>
<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
          integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
    <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
            integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
            integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>

    <script>
        $(document).ready(
        function() {
            $.ajax({
                type: 'GET',
                url: 'http://localhost:8080/dreamjob/city',
                contentType: 'application/json',
                dataType: 'json'
            })
                .done(function(response) {
                    let options = "";
                    let items = response.items;
                    for (let i = 0; i < items.length; i++) {
                        options += "<option value=\"" + items[i].city + "\">" + items[i].city + "</option>"
                    }
                    $('#city').html(options);
                })
                .fail(function(err) {
                    alert(err);
                });
        }   )
    </script>

    <title>Работа мечты</title>
</head>
<body>
<%
    String id = request.getParameter("id");
    Candidate candidate = new Candidate(0, "", 1, 0);
    if (id != null) {
        candidate = PsqlCandidateStore.instOf().findById(Integer.parseInt(id));
    }
%>
<script>
    function validate() {
        let result = true;
        if ($('#name').val() == '') {
            alert('Name field is empty');
            result = false;
        }
        return result;
    }
</script>
<div class="container pt-3">
    <div class="row">
        <div class="card" style="width: 100%">
            <div class="card-header">Новый кандидат</div>
            <div class="card-body">
                <form action="<%=request.getContextPath()%>/candidates.do?id=<%=candidate.getId()%>" method="post">
                    <div class="form-group">
                        <label for="name">Имя</label>
                        <input type="text" class="form-control" name="name" value="<%=candidate.getName()%>" id="name">
                    </div>
                    <div class="form-group">
                        <label for="city">Город</label>
                        <select type="text" class="custom-select" name="city" id="city">
                            <option value="empty">Empty</option>
                        </select>
                    </div>
                    <button type="submit" class="btn btn-primary" onclick="return validate();">Сохранить</button>
                </form>
            </div>
        </div>
    </div>
</div>
</body>
</html>
import random
import requests
import pytest
import os
import threading


BASE_URL = os.environ.get("machine_dns")  # internal or external test
numbers=["1","4","5"]
def test_added():
    response = requests.get(BASE_URL + "/added")
    print(response.text)
    assert response.status_code == 200

def test_added_num():
    for num in numbers:
        response = requests.get(BASE_URL + "/added/number/"+num)
        print(response.text)
        assert response.status_code == 200

def test_added_content_get():
    response = requests.get(BASE_URL + "/added/content")
    assert response.status_code == 200

def test_added_content_delete():
    response = requests.delete(BASE_URL + "/added/content")
    assert response.status_code == 200

def test_added_content_post():
    for num in numbers:
        data = {
            'num': num,
        }
        response = requests.get(BASE_URL + "/added/content",data=data)
        print(response.text)
        assert response.status_code == 200




if __name__ == "__main__":
    pytest.main()

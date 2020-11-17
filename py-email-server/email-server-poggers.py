import smtplib
import ssl
import sys
import socket 
from http.server import HTTPServer, BaseHTTPRequestHandler

'''
    run with python : python3 py-email-server-poggers
    dummy email is used to login and send emails
    runs locally for now but can throw it on aws and change
    server address here and in java code but this is fine 
    for now 

'''


#Define email_server credentials and SSL port
SSL_PORT = 465 #SSL PORT
SERVER_PORT = 10000
SERVER_ADDRESS = "localhost"

EMAIL_USER = "KellDougIncorporated"
EMAIL_PW = "kellydouge"

#Define send email function 
def send_email(receiver_email, message):
    with smtplib.SMTP_SSL("smtp.gmail.com", SSL_PORT, context=context) as server:
        server.login(EMAIL_USER, EMAIL_PW)
        #Send email here
        subject = "Subject: Calendar Notification";
        server.sendmail(EMAIL_USER, receiver_email, "{}\n\n{}".format(subject, message))

class HandlePost(BaseHTTPRequestHandler):
    #Send happy response back to client
    def _set_headers(self):
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()

    #override method to handle posted data
    def do_POST(self):
        self._set_headers()
        #Get size of data
        content_length = int(self.headers['Content-Length'])

        #Actually read data using content length 
        post_data = self.rfile.read(content_length)

        #Convert bytes to string and split message
        recv_email, message = post_data.decode("utf-8").split(",")
        
        #send mail poggers        
        send_email(recv_email, message)
        print(f"POSTED data: {post_data}")

#Create a secure SSL context
context = ssl.create_default_context()



#send_email("@live.kutztown.edu", "HEY MAN")

# Prep server_address 
server_address = (SERVER_ADDRESS, SERVER_PORT)

#Bind server configuration
httpd = HTTPServer(server_address, HandlePost)


print(f"Starting http server on {SERVER_ADDRESS}:{SERVER_PORT}")

#Server forever poggers
httpd.serve_forever()

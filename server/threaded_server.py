import socket
import select
import sys
import os
import errno
try:
    import threading
except ImportError:
    import dummy_threading as threading
def _eintr_retry(func, *args):
    """restart a system call interrupted by EINTR"""
    while True:
        try:
            return func(*args)
        except (OSError, select.error) as e:
            if e.args[0] != errno.EINTR:
                raise
class ThreadingTCPServer:

    address_family = socket.AF_INET

    socket_type = socket.SOCK_STREAM

    request_queue_size = 5

    allow_reuse_address = False

    timeout = None

    daemon_threads = True

    def __init__(self, server_address, RequestHandlerClass, bind_and_activate=True):
        self.server_address = server_address
        self.RequestHandlerClass = RequestHandlerClass
        self.__shutdown_request = False
        self.socket = socket.socket(self.address_family,
                                    self.socket_type)
        if bind_and_activate:
            self.server_bind()
            self.server_activate()

    def server_bind(self):
        if self.allow_reuse_address:
            self.socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.socket.bind(self.server_address)
        self.server_address = self.socket.getsockname()
        
    def server_activate(self):
        self.socket.listen(self.request_queue_size)

    def serve_forever(self, poll_interval=0.5):
        try:
            while not self.__shutdown_request:
                r, w, e = _eintr_retry(select.select, [self], [], [], poll_interval)
                if self in r:
                    self._handle_request_noblock()
        finally:
            self.stop()

    def stop(self):
        self.__shutdown_request = True
        self.socket.close()
        self.__shutdown_request = False
        raise SystemExit(0)

    def fileno(self):
        return self.socket.fileno()

    def _handle_request_noblock(self):
        try:
            request, client_address = self.get_request()
        except socket.error:
            return
        if self.verify_request(request, client_address):
            try:
                self.process_request(request, client_address)
            except:
                self.handle_error(request, client_address)
                self.shutdown_request(request)

    def handle_timeout(self):
        pass

    def verify_request(self, request, client_address):
        return True

    def process_request_thread(self, request, client_address):
        try:
            self.finish_request(request, client_address)
            #self.shutdown_request(request)
        except:
            self.handle_error(request, client_address)
            self.shutdown_request(request)

    def process_request(self, request, client_address):
        t = threading.Thread(target = self.process_request_thread,
                             args = (request, client_address))
        t.daemon = self.daemon_threads
        t.start()

    def get_request(self):
        return self.socket.accept()

    def finish_request(self, request, client_address):
        self.RequestHandlerClass(request, client_address, self)

    def shutdown_request(self, request):
        try:
            request.shutdown(socket.SHUT_WR)
        except socket.error:
            pass
        self.close_request(request)

    def close_request(self, request):
        request.close()
    def handle_error(self, request, client_address):
        print('-'*40)
        print('Exception happened during processing of request from', end=' ')
        print(client_address)
        import traceback
        traceback.print_exc()
        print('-'*40)


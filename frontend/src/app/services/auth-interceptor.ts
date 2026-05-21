import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');
  if (token) {
    const reqAuth = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
    return next(reqAuth);
  }
  return next(req);
};

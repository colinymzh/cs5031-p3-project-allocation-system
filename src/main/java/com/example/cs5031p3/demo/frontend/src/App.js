import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Route, Switch, Redirect } from 'react-router-dom';
import Login from './views/Login/Login';
import Home from './views/Home/Home';
import Account from './views/Account/Account';

/**
 * Main component of the application.
 *
 * @returns {JSX.Element} Main component JSX
 */
function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      setIsLoggedIn(true);
    }
  }, []);

  /**
   * Handle login action.
   */
  const handleLogin = () => {
    setIsLoggedIn(true);
  };

  /**
   * Handle logout action.
   */
  const handleLogout = () => {
    localStorage.removeItem('userInfo');
    setIsLoggedIn(false);
  };

  return (
    <Router>
      <Switch>
        <Route exact path="/">
          {isLoggedIn ? <Redirect to="/home" /> : <Redirect to="/login" />}
        </Route>
        <Route path="/login">
          <Login onLogin={handleLogin} />
        </Route>
        <Route path="/home">
          {isLoggedIn ? <Home onLogout={handleLogout} /> : <Redirect to="/login" />}
        </Route>
      </Switch>
    </Router>
  );
}

export default App;
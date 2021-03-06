import React, { Component } from 'react';
import { connect } from 'react-redux';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';

import Header from '../components/Header';
import Footer from '../components/Footer';
import LoginPage from './user/LoginPage';
import FrontPage from './FrontPage';
import CreateMeetingPage from './meeting/CreateMeetingPage';
import ShowMeetingPage from './meeting/ShowMeetingPage';

import './LayoutPage.css';

class LayoutPage extends Component {
  render() {
    return (
      <Router>
        <div className="LayoutPage">
          <Route render={(props) => {
            const isInvert = props.location.pathname === '/';
            return (
              <Header user={props.user} invert={isInvert} />
            );
          }} />
          <Switch>
            <Route exact path='/' component={FrontPage}/>
            <Route exact path='/login' component={LoginPage}/>
            <Route exact path='/meetings/create' component={CreateMeetingPage}/>
            <Route exact path='/meetings/:meetingId' component={ShowMeetingPage}/>
          </Switch>
          <Footer/>
        </div>
      </Router>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    user: state.user,
  };
}

export default connect(mapStateToProps)(LayoutPage);

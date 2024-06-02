<div align="center">
  <img src="https://github.com/thanoskalantzis/S.now/blob/main/Website%20%26%20PHP/cover.png" alt="logo" width="40%">
</div>

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About the Project</a></li>
    <li><a href="#key-features">Key Features</a></li>
    <li><a href="#built-with">Built with</a></li>
    <li>
      <div><a href="#deployment">Deployment</a></div>
      <ol>
        <li><a href="#web-server--mysql-database-configuration">Web Server & MySQL Database configuration</a></li>
        <li><a href="#android-application">Android Application</a></li>
      </ol>
    </li>
    <li>
      <div><a href="#usage">Usage</a></div>
      <ol>
        <li><a href="#employee-account">Employee Account</a></li>
        <li>
          <div><a href="#how-to-place-an-order">How to place an Order</a></div>
          <ol>
            <li><a href="#unique-key">Unique Key</a></li>
            <li><a href="#security-purposes">Security Purposes</a></li>
            <li><a href="#main-idea">Main Idea</a></li>
            <li><a href="#unique-key-construction">Unique Key construction</a></li>
          </ol>
        </li>
      </ol>
    </li>
    <li><a href="#better-approaches">Better Approaches</a></li>
    <li><a href="#disclaimer">Disclaimer</a></li>
  </ol>
</details>

# About the Project
An Android Application that manages Data stored online.

This Application was originally developed as part of a **University Project** but has also been enhanced along the way!

# Key Features
This Android Application allows:
1. Customers to place Orders directly from within a physical Store,
2. Employees of a Store to manage and edit Orders placed by Customers,
3. Business owners to add, remove, and modify Products.

# Built with
A plethora of technologies were used to build this Project:
- Java
- Android Studio
- PHP
- Online MySQL Database
- Free Web Hosting

# Deployment

### Web Server & MySQL Database configuration
A Web Hosting Service was originally utilized to host both an online MySQL Database and the necessary Web Server to handle the HTTP Requests being made.

Hosting the provided PHP files on this Web Server resulted in being able to execute all Server side operations of this System (e.g. accessing the Database, performing Database queries and retrieving the returned results, constructing JSON responses, delivering the necessary content to the Web Server, etc).

Thus, to successfully use this Application, both a Web Server and a MySQL Database should first be configured so that the following conditions are fulfilled:
<ol>
  <li>There should be a <strong>Database</strong> structure as the sample one provided.</li>
  <li>The <strong>SQL file</strong> provided under the <a href="https://github.com/thanoskalantzis/S.now/tree/main/Database">/Database</a> directory can be used to create the necessary Database structure and populate the Database Tables with sample Data.</li>
  <li>
    <div>The <strong>PHP files</strong> provided under the <a href="https://github.com/thanoskalantzis/S.now/tree/main/Website%20%26%20PHP/connect">/Website & PHP/connect</a> directory should be placed on the Web Server to be utilized.</div>
    <div>The provided PHP scripts can then be used to connect to the target Database and are the ones responsible for performing the necessary Database queries, constructing <strong>JSON responses</strong> based on the returned results, and delivering the necessary content per HTTP Request.</li>    
  <li>
    <div>With respect to the <strong>HTTP GET & POST Requests</strong> being performed:</div>
    <ol>
      <li>
        <div>All HTTP Requests are performed on <strong>URLs</strong> of the following <strong>structure</strong>:</div>
        <div>
          <html>
            <body>
              <pre><code>getString(R.string.BASE_URL).concat("/connect/{php_file}.php")</code></pre>
            </body>
          </html>
        </div>
        <div>
          <div>Where:</div>
          <ol>
            <li><code>getString(R.string.BASE_URL)</code>: returns the <code>{BASE_URL}</code> as defined within the <strong>String resources</strong> file (<a href="https://github.com/thanoskalantzis/S.now/blob/main/Android/app/src/main/res/values/strings.xml">strings.xml</a>),</li>
            <li>
              <div><code>{php_file}</code>: the name of the <strong>targeted PHP file</strong>.</div>
              <div>The targeted PHP file corresponds to the script file that will eventually process the HTTP Request on the Server side.</div>
            </li>
          </ol>
        </div>
      </li>
      <li>
        <div>The necessary <strong>query parameters</strong> are also constructed before an HTTP Request is performed.</div>
        <div>E.g. The necessary parameters used for <strong>Authentication</strong> purposes are the ones mentioned in the next <a href="#android-application">"Android Application"</a> section.</div>
      </li>
    </ol>
  </li>
</ol>

### Android Application
The Android Application should be built after the necessary values are provided for the following <strong>String resources</strong>:

<html>
  <body>
<pre><code>&lt;!-- CUSTOMIZE --&gt;
&lt;string name="BASE_URL"&gt;&lt;!-- PROVIDE BASE URL OF CUSTOM WEBSITE --&gt;&lt;/string&gt;
&lt;string name="DATABASE_SERVER"&gt;&lt;!-- PROVIDE DATABASE SERVER LOCATION --&gt;&lt;/string&gt;
&lt;string name="DATABASE_NAME"&gt;&lt;!-- PROVIDE DATABASE NAME --&gt;&lt;/string&gt;
&lt;string name="DATABASE_USERNAME"&gt;&lt;!-- PROVIDE DATABASE USERNAME --&gt;&lt;/string&gt;
&lt;string name="DATABASE_PASSWORD"&gt;&lt;!-- PROVIDE DATABASE PASSWORD --&gt;&lt;/string&gt;</code></pre>
  </body>
</html>

To customize the aforementioned String resources, the <a href="https://github.com/thanoskalantzis/S.now/blob/main/Android/app/src/main/res/values/strings.xml">strings.xml</a> file located under the <a href="https://github.com/thanoskalantzis/S.now/tree/main/Android/app/src/main/res/values">/Android/app/src/main/res/values</a> directory should be accordingly modified. 

After all aforementioned steps are performed, <strong>build</strong> the Android Project and <strong>enjoy!</strong>

# Usage

## Employee Account

Note, that for a registered Employee to be able to start working on Customer Orders being made in a particular Store, this Employee should have first been added to the workforce of this particular Store by the Store owner.

Note, that an Employee should still have first created an account (registered) to the Application.

For a Store owner to add an Employee to the workforce of a particular Store, the Store owner should first log in, select the corresponding "Add employee" option, and provide the `Tax Identification Number of that Employee`.

## How to place an Order

### Unique Key
For the time being, the Application prompts the Customer to provide a **unique key** before an Order is successfully sent.

### Security Purposes
This configuration has been set up in such a manner for **security purposes**:
1. To confirm the location of the Customer,
2. To uniquely identify the specific table at which the Customer is seated.

### Main Idea
To achieve this, the main idea was to let the Customer <strong>scan</strong> a <strong>unique QR Code</strong> being placed on each table in a Store.

This QR Code will correspond to a unique value.

Upon that value is used, the System will be able to access the aforementioned information and the Order will automatically be sent.

If a valid QR Code is used by the Customer to uniquely verify that is indeed at the Store's location from which the Customer tries to place an Order, then the Order will successfully be sent to the Store.

### Unique Key construction
For the time being, this unique key is constructed as follows:

`{Tax Identification Number of the Store} + {Number of the specific table at which the Customer is seated}`

# Better Approaches
A better approach would be to replace the aforementioned functionality by letting the Customer directly proceed to checkout (paying stage).

# Disclaimer
<strong>
  <ul>
    <li>All Data used in this Project are made up / fictitious and do not correspond to reality.</li>
    <li>Any reference to existing Businesses / or Individuals / or any similarity to actual existing Data is purely coincidental.</li>
    <li>All Data has been purely created with the sole purpose of being used for demonstration purposes.</li>
    <li>This Project is strictly educational and shall only be used for informational and demonstration purposes.</li>
    <li>Neither this Project nor any part of it should be used in Production Environments / Systems.</li>
    <li>Do not use this Project or any part of it for commercial purposes or monetary gain.</li>
  </ul>
</strong>

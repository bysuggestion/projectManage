<%@ page language="java" contentType="text/html; charset=utf-8"%>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <script >
   function login(){
	   alert("还未登陆，请先登陆");
   }

    </script>
    
	<!-- SITE HEADER -->
	<header id="site-header" role="banner">
		<!-- HEADER TOP -->
		<div class="header-top">
			<div class="container">
				<div class="row">
					<div class="col-xs-12 col-sm-6 col-md-7">
						<!-- CONTACT INFO -->
						<div class="contact-info">
							<i class="iconfont-headphones round-icon"></i>
							<strong>800-820-8820</strong>
							<span>(Mon- Fri: 09.00 - 21.00)</span>
							</div>
						<!-- // CONTACT INFO -->
					</div>
					<div class="col-xs-12 col-sm-6 col-md-5">
						<ul class="actions unstyled clearfix">
							<li>
								<!-- SEARCH BOX -->
								<div class="search-box">
									<form action="#" method="post">
										<div class="input-iconed prepend">
											<button class="input-icon"><i class="iconfont-search"></i></button>
											<label for="input-search" class="placeholder">想找什么。。。</label>
											<input type="text" name="q" id="input-search"  required />
										</div>
									</form>
								</div>
								<!-- // SEARCH BOX -->
							</li>

							<li data-toggle="sub-header" data-target="#sub-cart">
								<!-- SHOPPING CART -->
								<a href="javascript:void(0);" id="total-cart">
									<span>购物车<i class="iconfont-shopping-cart round-icon"></i></span>
								</a>
								
								<div id="sub-cart" class="sub-header">
									<div class="cart-header">
										<span>你的购物车空空如也</span>
										<small><a href="cart.jgp">查看购物车</a></small>
									</div>
									<ul class="cart-items product-medialist unstyled clearfix"></ul>
									<div class="cart-footer">
										<div class="cart-total clearfix">
											<span class="pull-left">总计：</span>
											<span class="pull-right total">¥ 0</span>
										</div>
										<div class="text-right">
											<a href="cart.jsp" class="btn btn-default btn-round view-cart" onClick="displayCart()">购物车</a>
											
										</div>
									</div>
								</div>
								<!-- // SHOPPING CART -->
							</li>
						</ul>
					</div>
				</div>
			</div>
			
			<!-- ADD TO CART MESSAGE -->
			<div class="cart-notification">
				<ul class="unstyled"></ul>
			</div>
			<!-- // ADD TO CART MESSAGE -->
		</div>
		<!-- // HEADER TOP -->
		<!-- MAIN HEADER -->
		<div class="main-header-wrapper">
			<div class="container">
				<div class="main-header">

					<!-- SITE LOGO -->
					<div class="logo-wrapper">
						<a href="index.jsp" class="logo" title="悦书">
							<font>悦书</font>
						</a>
					</div>
					<!-- // SITE LOGO -->
					<!-- SITE NAVIGATION MENU -->
					<nav id="site-menu" role="navigation">
						<ul class="main-menu  hidden-xs">
						  <c:choose>
						  <c:when test="${empty loginName}">
							<li class="active">
								<a href="Login.jsp" onClick="login()">我的悦书</a>
							</li>
							<li>
								<a href="Product?action=listNew">新书</a>
							</li>
							<li>
								<a href="Product?action=listOld">二手书</a>
							</li>
							<li>
								<a href="aboutUs.jsp">About Us</a>
							</li>
						  
						  
							<li>
								<a href="Login.jsp">&nbsp;&nbsp;>>>登陆</a>
							</li>
							<li>
								<a href="Register.jsp">注册</a>
							</li>
							</c:when>
							
							<c:otherwise>
							<li class="active">
								<a href="User?action=show&id=${userId}">我的悦书</a>
							</li>
							<li>
								<a href="Product?action=list">新书</a>
							</li>
							<li>
								<a href="Product?action=list">二手书</a>
							</li>
							<li>
								<a href="aboutUs.jsp">About Us</a>
							</li>
							<li>
							           <a href=#>欢迎${loginName}</a>
							</li>
							<li>
								<a href="User?action=logout">&nbsp;&nbsp;注销</a>
							</li>
							</c:otherwise>
						   </c:choose>
						</ul>

					</nav>
					<!-- // SITE NAVIGATION MENU -->
				</div>
			</div>
		</div>
		<!-- // MAIN HEADER -->
	</header>
	<!-- // SITE HEADER -->

import React, { useState } from "react";
import { Mail, Lock, LogIn, UserPlus } from 'lucide-react';
import { useNavigate } from "react-router-dom";


function LoginForm() {
    const [isLoginMode, setIsLoginMode] = useState(true);

    const [firstName, setFirstName] = useState("");
    const [lastName, setLastName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const navigate = useNavigate();



    const handleSubmit = async (e) => {
        e.preventDefault();
        const url = isLoginMode
            ? "http://localhost:8080/api/req/login"
            : "http://localhost:8080/api/req/signup";

        const payload = isLoginMode
            ? { email, password }
            : { firstName, lastName, email, password};

        try {
            const response = await fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(payload),
            });

            if (!response.ok) throw new Error("Erreur serveur");


            const responseData = await response.json();
    
            if (responseData.token) {
                localStorage.setItem("token", responseData.token);
                localStorage.setItem("professeurId", responseData.id);

            } else {
                throw new Error("Aucun token trouvé dans la réponse");
            }

            if (!isLoginMode) {
              setFirstName("");
              setLastName("");
            }
            setEmail("");
            setPassword("");
            
            navigate("/dashboard");


        } catch (error) {
            console.error(error);
            alert("Échec de la connexion / inscription");
        }
    };


    return (
        <div className="grid h-screen place-items-center">

            <div className="w-[500px] bg-white p-8 rounded-1xl shadow-2xl">
                {/* Header Titles */}
                <div className="flex justify-center mb-4">
                    <div className="text-center">
                        <h2 className="text-3xl font-bold ">
                            <p>Gestion des Absences</p>
                        </h2>

                        <p className="text-gray-500 text-base font-50 mt-2">Plateforme pour les enseignants</p>

                    </div>
                </div>

                {/* Tab Controls */}
                <div className="relative flex h-12 mb-6 bg-gray-100 overflow-hidden rounded-lg mt-10">
                    <button
                        className={`w-1/2 text-lg font-semibold transition-all z-10 ${
                            isLoginMode ? "text-black" : "text-gray-400"
                        }`}
                        onClick={() => setIsLoginMode(true)}
                    >
                        Connexion
                    </button>
                    <button
                        className={`w-1/2 text-lg font-semibold transition-all z-10 ${
                            !isLoginMode ? "text-black" : "text-gray-400"
                        }`}
                        onClick={() => setIsLoginMode(false)}
                    >
                        Inscription
                    </button>
                    <div
                        className={`absolute  top-1 left-[4px] h-[82%] w-1/2 bg-white transition-all rounded-lg shadow  ${
                            isLoginMode ? "translate-x-0" : "translate-x-52"
                        }`}
                    ></div>
                </div>

                {/* Form Section */}
                <form className="space-y-4" onSubmit={handleSubmit}>
                    {/* Signup-only Field */}
                    {!isLoginMode && (
                        <div className="flex gap-4">
                            <input
                                type="text"
                                placeholder="Prénom"
                                required
                                className="w-1/2 p-3 border-b-2 border-gray-300 outline-none focus:border-cyan-500 placeholder-gray-400"
                                value={firstName}
                                onChange={(e) => setFirstName(e.target.value)}
                            />
                            <input
                                type="text"
                                placeholder="Nom"
                                required
                                className="w-1/2 p-3 border-b-2 border-gray-300 outline-none focus:border-cyan-500 placeholder-gray-400"
                                value={lastName}
                                onChange={(e) => setLastName(e.target.value)}
                            />
                        </div>
                    )}

                    {/* Shared Fields */}
                    <div className="flex items-center border-b-2 border-gray-300 focus-within:border-cyan-500">
                        <Mail className="text-gray-400 mr-2" size={20}/>
                        <input
                            type="email"
                            placeholder="Adresse Email"
                            required
                            className="w-full p-3 outline-none placeholder-gray-400"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </div>
                    <div className="flex items-center border-b-2 border-gray-300 focus-within:border-cyan-500">
                        <Lock className="text-gray-400 mr-2" size={20}/>
                        <input
                            type="password"
                            placeholder="Mot de passe"
                            required
                            className="w-full p-3 outline-none placeholder-gray-400"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                    </div>

                    {/* Forgot Password (Only for Login) */}
                    {isLoginMode && (
                        <div className="text-right">
                            <a href="#" className="text-cyan-600 hover:underline">
                                Mot de Passe Oublié?
                            </a>
                        </div>
                    )}

                    {/* Submit Button */}
                    <div className="space-y-4">
                        {isLoginMode ? (
                            <button type="submit"
                                    className="w-full flex items-center justify-center gap-2 bg-black hover:bg-cyan-600 text-white py-2 px-4 rounded-lg shadow">
                                <LogIn size={18}/>
                                Se connecter
                            </button>
                        ) : (
                            <button type="submit"
                                    className="w-full flex items-center justify-center gap-2 bg-black hover:bg-cyan-600 text-white py-2 px-4 rounded-lg shadow">
                                <UserPlus size={18}/>
                                S'inscrire
                            </button>
                        )}
                    </div>

                    {/* Switch Mode Link */}
                    <p className="text-center text-gray-600">
                        {isLoginMode ? "Vous n'avez pas de compte? " : "Vous avez déjà un compte? "}
                        <a
                            href="#"
                            onClick={(e) => {
                                e.preventDefault();
                                setIsLoginMode(!isLoginMode);
                            }}
                            className="text-cyan-600 hover:underline"
                        >
                            {isLoginMode ? "Inscrivez-vous" : "Se connecter"}
                        </a>
                    </p>
                </form>
            </div>
        </div>

    );
            }

            export default LoginForm;